package micahgemmell.com.mtg_deck_l.helpers;
/*
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

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class CropTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        // dimensions for magiccards.info cards...(source, 18, 46, 275, 200)
        Bitmap squaredBitmap = Bitmap.createBitmap(source, 18, 46, 275, 200);
        if (squaredBitmap != source) {
            source.recycle();
        }
        return squaredBitmap;
    }

    @Override
    public String key() {
        return "MagicCrop";
    }
}
