# Copyright (C) 2019 The Dagger Authors.
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
#   Tests for internal code for implementing Hilt processors.

load("@rules_java//java:defs.bzl", "java_library", "java_test")

package(default_visibility = ["//:src"])

java_test(
    name = "ProcessorsTest",
    size = "small",
    srcs = ["ProcessorsTest.java"],
    deps = [
        "//hilt-compiler/main/java/dagger/hilt/processor/internal:processors",
        "//third_party/java/javapoet",
        "//third_party/java/junit",
        "//third_party/java/truth",
    ],
)

java_library(
    name = "generated_import",
    srcs = ["GeneratedImport.java"],
)

filegroup(
    name = "srcs_filegroup",
    srcs = glob(["**/*"]),
)
