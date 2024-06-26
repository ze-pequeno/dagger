/*
 * Copyright (C) 2022 The Dagger Authors.
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

package dagger.functional.kotlinsrc.names

import com.google.common.truth.Truth.assertThat
import dagger.Component
import javax.inject.Inject
import javax.inject.Provider
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** This is a regression test for https://github.com/google/dagger/issues/3069. */
@RunWith(JUnit4::class)
class ComponentFactoryNameConflictsTest {
  // A class name "Create" so the private method name in fastInit mode conflicts with the create()
  // factory method.
  class Create
  // Take a dependency so we don't just inline this directly.
  @Inject
  internal constructor(@Suppress("UNUSED_PARAMETER") provider: Provider<CreateUsage>)

  // Use another class to make sure the create class is used but not a direct component entry point.
  class CreateUsage @Inject internal constructor(@Suppress("UNUSED_PARAMETER") create: Create)

  @Component
  internal interface CreateComponent {
    fun createUsage(): CreateUsage
  }

  @Test
  fun testCreate() {
    val testComponent = DaggerComponentFactoryNameConflictsTest_CreateComponent.create()
    val createUsage = testComponent.createUsage()
    assertThat(createUsage).isNotNull()
  }

  // A class name "Builder" so the private method name in fastInit mode conflicts with the builder()
  // factory method.
  class Builder
  // Take a dependency so we don't just inline this directly.
  @Inject
  internal constructor(@Suppress("UNUSED_PARAMETER") provider: Provider<BuilderUsage>)

  class BuilderUsage @Inject internal constructor(@Suppress("UNUSED_PARAMETER") create: Builder)

  @Component
  internal interface BuilderComponent {
    fun builderUsage(): BuilderUsage

    @Component.Builder
    interface OtherBuilder {
      fun build(): BuilderComponent
    }
  }

  // Technically this test passes without claiming the name "builder" when we add the method (even
  // though we do anyway for safety) because KeyVariableNamer actually hardcodes a list of common
  // names to avoid which includes "builder".
  @Test
  fun testBuilder() {
    val testComponent = DaggerComponentFactoryNameConflictsTest_BuilderComponent.builder().build()
    val builderUsage = testComponent.builderUsage()
    assertThat(builderUsage).isNotNull()
  }
}
