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

package dagger.functional.kotlinsrc.modules.subpackage

import dagger.Module
import dagger.Provides

/**
 * We needed a separate test for non-abstract transitively included pkg-private modules. The reason
 * is that this caused a build failures when the component was generated in a separate package
 * because the generated no-op method references the inaccessible package-private type, so we
 * omitted those no-op methods to support such modules.
 */
@Module
internal object NonAbstractPackagePrivateModule {
  @Provides
  fun provideFoo(): FooForProvision {
    return FooForProvision()
  }
}
