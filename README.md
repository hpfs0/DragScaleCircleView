# DragScaleCircleView
a custom imageview that provides dragged and scaled

## How does it look?
![image](https://github.com/hpfs0/DragScaleCircleView/blob/master/show.gif)

## Why?
Sometimes need to cut a picture into a circle.

## Dependency
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

## Download
The latest version can be downloaded as a zip and referenced by your application as a library project.

## TODO
* ~~make the circle window move and drag on imageview.~~
* ~~fix move the circle window right edge bug.~~
* ~~impprove performence when the circle window move and drag.~~
* crop the circle window from imageview.
* add rect window option.
* add custom properties of guideline.(et. on/off、size、color).
* add custom properties of border.(et. size、color).

## License

    Copyright 2015 hpfs0 inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
