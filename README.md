# DragScaleCircleView
a custom imageview that provides dragged and scaled

## How does it look?
![image](https://github.com/hpfs0/DragScaleCircleView/blob/master/show.gif)

## Why?
Sometimes need to cut a picture into a circle.

## Install
The lib is published on Jcenter:

```java
    compile 'com.rori.zenvo.dragscalecircleview:dragscalecircleview:1.0.0'
```

## Usage
To add the DragScaleCircleView to your application, specify com.edmodo.cropper.CropImageView in your layout XML.

```xml
    <com.rori.zenvo.dragscalecircleview.DragScaleCircleView
        android:id="@+id/dragScaleCircleView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:clickable="true"
        android:src="@drawable/img1"/>
```

## Customization
The default dragScaleCircleView to show the guideline.
If you want to guideline don't shown, please define the <code>xmlns:dragscalecircleview="http://schemas.android.com/apk/res-auto"</code> on your layout file.

custom attrs:

<code>dragscalecircleview:hasGuideLine="false"</code>

<code>dragscalecircleview:guideLineSize="5"</code>

<code>dragscalecircleview:guideLineColor="@android:color/guideLineColor"</code>

<code>dragscalecircleview:borderSize="5"</code>

<code>dragscalecircleview:borderColor="@android:color/borderColor"</code>


to <code>com.rori.zenvo.dragscalecircleview.DragScaleCircleView</code>. Just like blew.

```xml
    <com.rori.zenvo.dragscalecircleview.DragScaleCircleView
        android:id="@+id/dragScaleCircleView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        dragscalecircleview:hasGuideLine="false"
        android:clickable="true"
        android:src="@drawable/img1"/>
```

## Download
The latest version can be downloaded as a zip and referenced by your application as a library project.

## TODO
- [x] make the circle window move and drag on imageview.
- [x] fix move the circle window right edge bug.
- [x] impprove performence when the circle window move and drag.
- [ ] crop the circle window from imageview.
- [x] add custom properties of guideline.(et. on/off、size、color).
- [x] add custom properties of border.(et. size、color).

## License

    Copyright 2015 hpfs0

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
