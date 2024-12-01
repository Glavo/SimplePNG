/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.png;

public enum PNGType {
    GRAYSCALE(0, 1),
    RGB(2, 3),
    PALETTE(3, 1),
    GRAYSCALE_ALPHA(4, 2),
    RGBA(6, 4);

    final int id;
    final int cpp;

    PNGType(int id, int cpp) {
        this.id = id;
        this.cpp = cpp;
    }
}
