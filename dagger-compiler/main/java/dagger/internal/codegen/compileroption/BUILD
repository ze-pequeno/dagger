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
#   Sources related to compiler options.

load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//dagger-compiler:internal"])

java_library(
    name = "compileroption",
    srcs = glob(["*.java"]),
    plugins = ["//dagger-compiler/main/java/dagger/internal/codegen/bootstrap"],
    tags = ["maven:merged"],
    deps = [
        "//dagger-compiler/main/java/dagger/internal/codegen/xprocessing",
        "//dagger-runtime/main/java/dagger:core",
        "//dagger-spi",
        "//third_party/java/auto:common",
        "//third_party/java/guava/base",
        "//third_party/java/guava/collect",
        "//third_party/java/jsr330_inject:jsr330_inject_without_dagger",
    ],
)
