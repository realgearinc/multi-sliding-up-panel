### Android Multi Slide Up Panel

This library provides an advanced way to add a multi draggable sliding up panel
to your android project it's smoother than most of the other libraries available
it's fully coded in java and easy to extend functionality a demo project is
provided to see how this library is implemented.

### Demo

<img src="https://github.com/realgearinc/multi-sliding-up-panel/blob/master/src/demo.gif?raw=true" width="20%" height="25%" />

### Importing the Library

You can alternatively watch the [Tutorial](https://youtu.be/qysNPrAF5HQ) on youtube.

Simply add jitpack to your `settings.gradle` file

```groovy
dependencyResolutionManagement {
    ...
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Then add the following dependency to your project level `build.gradle` file
to use the latest version:

```groovy
dependencies {
    ...
    implementation 'com.github.realgearinc:multi-sliding-up-panel:1.2.1'
}
```

### Usage of Library

* Include `com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout` in
  your xml layout file set `layout_width` and `layout_height` attribute to
  `match_parent` and set your own `id` here I'll use `multiSlideUpPanel` as
  id.
* You must add 1 child view only using xml it can be a viewpager, framelayout etc...
  with `layout_width` and `layout_height` attribute to
  `match_parent`

Please refer to sample code below to understand more.

```xml
<com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout
    android:id="@+id/multiSlidingUpPanel"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- 1 child view is required -->
    <androidx.viewpager.widget.ViewPager
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
            
</com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout>
```

* Now to add a slide panel you will have to create a new layout file and extend
  `BasePanelView` class then write a few lines of code.

* Create a layout file, I'll name my layout file `layout_panel` you can add a
  `TextView` inside it.

Sample code of my layout file.

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/textView"
        android:text="Panel Count"
        android:textSize="18sp"
        android:textStyle="bold"
        android:gravity="center"
        android:textAlignment="center"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</FrameLayout>
```

* Now you have to create a new java file, I'll name it `Panel` you can
  set a different name then extend it to `BasePanelView`

Sample Code:

```groovy
...

import com.realgear.multislidingpanel.BasePanelView;

public class Panel extends BasePanelView {

}
```

* Next we'll have to implement three methods `onCreateView()`,`onBindView()`
  and `onPanelStateChanged(int panelState)` and "create a constructor matching
  our super" inside the constructor add another parameter for
  `MultiSlidingUpPanelLayout` refer to sample code for more info.

Sample Code inside `Panel` class:

```groovy
    @Override
    public Panel(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context);
    }

    @Override
    public void onCreateView() { }

    @Override
    public void onBindView() { }

    @Override
    public void onPanelStateChanged(int panelSate) { }
```

* Now inside the constructor below add the following lines.

```groovy
// These 2 lines are required 
getContext().setTheme(R.style.Theme_YOUR_PROJECTS_THEME); // Your projects theme
LayoutInflater.from(getContext().inflate(R.layout.layout_panel, this, true);
```

* Next add these lines inside `onCreateView()` void.

```groovy
// The panel will be collapsed on start of application
this.setPanelState(MultiSlidingUpPanelLayout.COLLAPSED);

// The panel will slide up and down
this.setSlideDirection(MultiSlidingUpPanelLayout.SLIDE_VERTICAL)

// Sets the panels peak height
this.setPeakHeight(142);
```

* The required code is done inside `Panel` now we will go to our `MainActivity` class
  and add these lines inside `onCreate()` void.

```groovy
    MultiSlidingUpPanelLayout panelLayout = findViewById(R.id.multiSlidingUpPanel);
    
    List<Class<?>> items = new ArrayList<>();
    
    // You add your panels here it can be different classes with different layouts
    // but they all should extend the BasePanelView class with same constructors
    // You can add 1 or more then 1 panels
    
    items.add(Panel.class);
    items.add(Panel.class);
    items.add(Panel.class);
    
    // This is to listen on all the panels you can add methods or extend the class
    panelLayout.setPanelStateListener(new PanelStateListener(panelLayout) {});
    
    // The adapter handles the items you can also extend it but I don't recommend for
    // beginners
    panelLayout.setAdapter(new Adapter(getBaseContext(), items) {});
```

* That's all

### Implementation

This library is based on the open-source [SlidingUpPanelLayout](https://github.com/woxingxiao/SlidingUpPanelLayout) library.
Thanks to [woxingxiao](https://github.com/woxingxiao)

### Requirements

Tested on Android 7.0+

### License

> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this work except in compliance with the License.
> You may obtain a copy of the License in the LICENSE file, or at:
>
>  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
