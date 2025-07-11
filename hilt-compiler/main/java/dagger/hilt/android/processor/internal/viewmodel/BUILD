# Copyright (C) 2020 The Dagger Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Description:
#   ViewModelInject processor.
load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")
load("@rules_java//java:defs.bzl", "java_plugin")
load("//tools:processor_compat.bzl", "compat_processor_plugin")

package(default_visibility = ["//:src"])

compat_processor_plugin(
    name = "processor",
    generates_api = 1,
    javac_processor_name = "ViewModelProcessor",
    processor_lib_dep = ":processor_lib",
)

kt_jvm_library(
    name = "processor_lib",
    srcs = [
        "KspViewModelProcessor.kt",
        "ViewModelMetadata.kt",
        "ViewModelModuleGenerator.kt",
        "ViewModelProcessingStep.kt",
        "ViewModelProcessor.kt",
    ],
    deps = [
        "//dagger-compiler:xprocessing",
        "//hilt-compiler/main/java/dagger/hilt/android/processor/internal:android_classnames",
        "//hilt-compiler/main/java/dagger/hilt/processor/internal:base_processor",
        "//hilt-compiler/main/java/dagger/hilt/processor/internal:classnames",
        "//hilt-compiler/main/java/dagger/hilt/processor/internal:compiler_options",
        "//hilt-compiler/main/java/dagger/hilt/processor/internal:processor_errors",
        "//hilt-compiler/main/java/dagger/hilt/processor/internal:processors",
        "//third_party/java/auto:service",
        "//third_party/java/guava/collect",
        "//third_party/java/incap",
        "//third_party/java/javapoet",
        "//third_party/kotlin/kotlin_symbol_processing:symbol-processing-api",
    ],
)

java_plugin(
    name = "validation_plugin",
    deps = [":validation_plugin_lib"],
)

kt_jvm_library(
    name = "validation_plugin_lib",
    srcs = [
        "ViewModelValidationPlugin.kt",
    ],
    deps = [
        "//dagger-compiler:xprocessing",
        "//dagger-spi",
        "//hilt-compiler/main/java/dagger/hilt/android/processor/internal:android_classnames",
        "//hilt-compiler/main/java/dagger/hilt/processor/internal:dagger_models",
        "//third_party/java/auto:service",
        "//third_party/java/guava/graph",
        "//third_party/java/javapoet",
    ],
)

# See: https://github.com/bazelbuild/rules_kotlin/issues/324
alias(
    name = "libprocessor_lib-src.jar",
    actual = ":processor_lib-sources.jar",
)

alias(
    name = "libvalidation_plugin_lib-src.jar",
    actual = ":validation_plugin_lib-sources.jar",
)

filegroup(
    name = "srcs_filegroup",
    srcs = glob(["*"]),
)
