/*
 * Copyright (C) 2020 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger.hilt.android.plugin.task

import com.android.build.gradle.api.UnitTestVariant
import dagger.hilt.android.plugin.AndroidEntryPointClassTransformer
import dagger.hilt.android.plugin.HiltExtension
import dagger.hilt.android.plugin.util.getCompileKotlin
import dagger.hilt.android.plugin.util.isClassFile
import dagger.hilt.android.plugin.util.isJarFile
import java.io.File
import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper

/**
 * Task that transform classes used by host-side unit tests. See b/37076369
 */
abstract class HiltTransformTestClassesTask @Inject constructor(
  private val workerExecutor: WorkerExecutor
) : DefaultTask() {

  @get:Classpath
  abstract val compiledClasses: ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  @Suppress("UnstableApiUsage") // Worker API
  internal interface Parameters : WorkParameters {
    val name: Property<String>
    val compiledClasses: ConfigurableFileCollection
    val outputDir: DirectoryProperty
  }

  @Suppress("UnstableApiUsage") // Worker API
  abstract class WorkerAction : WorkAction<Parameters> {
    override fun execute() {
      val outputDir = parameters.outputDir.asFile.get()
      outputDir.deleteRecursively()
      outputDir.mkdirs()

      val allInputs = parameters.compiledClasses.files.toList()
      val classTransformer = AndroidEntryPointClassTransformer(
        taskName = parameters.name.get(),
        allInputs = allInputs,
        sourceRootOutputDir = outputDir,
        copyNonTransformed = false
      )
      // Parse the classpath in reverse so that we respect overwrites, if it ever happens.
      allInputs.reversed().forEach {
        if (it.isDirectory) {
          it.walkTopDown().forEach { file ->
            if (file.isClassFile()) {
              classTransformer.transformFile(file)
            }
          }
        } else if (it.isJarFile()) {
          classTransformer.transformJarContents(it)
        }
      }
    }
  }

  @TaskAction
  fun transformClasses() {
    @Suppress("UnstableApiUsage") // Worker API
    workerExecutor.noIsolation().submit(WorkerAction::class.java) {
      it.compiledClasses.from(compiledClasses)
      it.outputDir.set(outputDir)
      it.name.set(name)
    }
  }

  internal class ConfigAction(
    private val outputDir: File,
    private val inputClasspath: FileCollection
  ) : Action<HiltTransformTestClassesTask> {
    override fun execute(transformTask: HiltTransformTestClassesTask) {
      transformTask.description = "Transforms AndroidEntryPoint annotated classes for JUnit tests."
      transformTask.outputDir.set(outputDir)
      transformTask.compiledClasses.from(inputClasspath)
    }
  }

  companion object {

    private const val TASK_PREFIX = "hiltTransformFor"

    fun create(
      project: Project,
      unitTestVariant: UnitTestVariant,
      extension: HiltExtension
    ) {
      if (!extension.enableTransformForLocalTests) {
        // Not enabled, nothing to do here.
        return
      }

      // TODO(danysantiago): Only use project compiled sources as input, and not all dependency jars
      // Using 'null' key to obtain the full compile classpath since we are not using the
      // registerPreJavacGeneratedBytecode() API that would have otherwise given us a key to get
      // a classpath up to the generated bytecode associated with the key.
      val inputClasspath =
        project.files(unitTestVariant.getCompileClasspath(null))

      // Find the test sources Java compile task and add its output directory into our input
      // classpath file collection. This also makes the transform task depend on the test compile
      // task.
      val testCompileTaskProvider = unitTestVariant.javaCompileProvider
      inputClasspath.from(testCompileTaskProvider.map {
        @Suppress("UnstableApiUsage") // Lazy property APIs
        it.destinationDirectory
      })

      // Similarly, if the Kotlin plugin is configured, find the test sources Kotlin compile task
      // and add its output directory to our input classpath file collection.
      project.plugins.withType(KotlinBasePluginWrapper::class.java) {
        val kotlinCompileTaskProvider = getCompileKotlin(unitTestVariant, project)
        inputClasspath.from(kotlinCompileTaskProvider.map {
          @Suppress("UnstableApiUsage") // Lazy property APIs
          it.destinationDirectory
        })
      }

      // Create and configure the transform task.
      val outputDir =
        project.buildDir.resolve("intermediates/hilt/${unitTestVariant.dirName}Output")
      val hiltTransformProvider = project.tasks.register(
        "$TASK_PREFIX${unitTestVariant.name.capitalize()}",
        HiltTransformTestClassesTask::class.java,
        ConfigAction(outputDir, inputClasspath)
      )
      // Map the transform task's output to a file collection.
      val outputFileCollection =
        project.files(hiltTransformProvider.map { it.outputDir })

      // Configure test classpath by appending the transform output file collection to the start of
      // the test classpath so they override the original ones. This also makes test task (the one
      // that runs the tests) depend on the transform task.

      @Suppress("UNCHECKED_CAST")
      val testTaskProvider = project.tasks.named(
        "test${unitTestVariant.name.capitalize()}"
      ) as TaskProvider<Test>
      testTaskProvider.configure {
        it.classpath = outputFileCollection + it.classpath
      }
    }
  }
}
