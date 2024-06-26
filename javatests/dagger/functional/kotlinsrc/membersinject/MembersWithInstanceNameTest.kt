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

package dagger.functional.kotlinsrc.membersinject

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MembersWithInstanceNameTest {
  @Suppress("BadInject") // This is intentional for testing purposes.
  internal class Foo @Inject constructor() {
    // Checks that member injection fields can use "instance" as a name (b/175818837).
    @Inject lateinit var instance: String
  }

  @Module
  internal interface TestModule {
    companion object {
      @Provides fun provideString(): String = "test"
    }
  }

  @Component(modules = [TestModule::class])
  internal interface TestComponent {
    fun foo(): Foo
  }

  @Test
  fun testMemberWithInstanceName() {
    val component = DaggerMembersWithInstanceNameTest_TestComponent.create()
    val foo = component.foo()
    assertThat(foo).isNotNull()
    assertThat(foo.instance).isEqualTo("test")
  }
}
