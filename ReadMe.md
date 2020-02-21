# Glata
Glata is a library for creating animations for android using OpenGL ES. It can be handy
when you have animations with lots of objects and/or large textures where Canvas is not fast enough.

## Attention!
This is an early alpha version and its functionality is quite poor.
Please, be patient and wait for updates.

## How to use this library in your project?

Just add `android.opengl.GLSurfaceView` to the xml layout, you can specify any width and height

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.opengl.GLSurfaceView
        android:id="@+id/surfaceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

Now, you have to extend com.yalantis.glata.core.scene.Scene class and override onAttach method.
This is where the initialization of your animation scene happens. Here you create shaders and
objects, specify the animations.
```kotlin
    class TestScene(sp: SceneParams = SceneParams()) : Scene(sp) {
          override fun onAttach(rp: RendererParams) {
                super.onAttach(rp)
                rp.managers.shaderManager.add(Shaders.DEFAULT_COLOR_SHADER) 
                rp.managers.shaderManager.add(Shaders.DEFAULT_TEXTURE_SHADER)
        
                ...
            }

    }
```

You have to create a rectangle, specify its color or texture, attach a shader and create animation.
Currently you can choose only movement, scaling and rotating animations (or several at once and
wrap them into AnimationList).
```kotlin
    val rect1 = Rectangle.builder()
                    .setGradientColor(Color(1f, 0f, 0f, 1f), Color(0f, 1f, 0f, 1f),
                            Color(0f, 0f, 1f, 1f), Color(0.6f, 0.6f, 0.6f, 1f))
                    .build()
            rect1.mp.setShader(Shaders.DEFAULT_COLOR_SHADER)
            rect1.animation = ScalingAnimation(0.8f, 1f, 1000f).apply {
                isInfinite = true
                interpolator = AccelerateDecelerateInterpolator()
            }
            rect1.mp.transform.position.x = -1.1f
            children.add(rect1)
            
    val rect2 = Rectangle(1f, 1f)
            rect2.animation = AxisMovementAnimation(Axis.Y, rowHeight, rowHeight + 1.1f, 1000f)
                    .apply {
                        isInfinite = true
                        returnToInitialAfterFinished = true
                    }
            rect2.mp.textureId = rp.managers.textureManager.add(Texture(rp, "ava_2"))
            rect2.mp.setShader(Shaders.DEFAULT_TEXTURE_SHADER)
            rect2.mp.transform.position.y = rowHeight
            children.add(rect2)
```

Now, set up your camera. You can choose vertical or horizontal axis to match glSurfaceView's size and
define the scale - set the number of units you want to see on this axis. Another axis will
adjust automatically according to the glSurfaceView's proportions.
```kotlin
    override fun onSurfaceChanged(rp: RendererParams, width: Int, height: Int) {
            super.onSurfaceChanged(rp, width, height)
            if (width > height) {
                sp.camera.setVerticalSizeInUnits(3.2f)
            } else {
                sp.camera.setHorizontalSizeInUnits(3.2f)
            }
            sp.camera.setPivot(Align.CENTER)
            sp.camera.setProjectionMatrix()
        }
```

Then you just need to initialize this surfaceView by calling `initSurface` or 
`initSurfaceTransparent` and pass there context and your animation scene.
```kotlin
    surfaceView.initSurface(context, TestScene())
``` 

That's all! Now you have your animation!


## Let us know!

We’d be really happy if you sent us links to your projects where you use our component. 
Just send an email to github@yalantis.com And do let us know if you have any questions or suggestion regarding the animation.

## License

	The MIT License (MIT)

	Copyright © 2019 Yalantis, https://yalantis.com

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
