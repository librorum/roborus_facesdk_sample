# Roborus Face SDK Sample App

## Preparation

* Copy library jar file into /libs folder.
* Modify app's build.gradle like below
	```
	repositories {
		maven { url 'https://maven.fabric.io/public' }
		flatDir { dirs 'libs' }
	}

	dependencies {
		// some other libraries ...
		implementation files('libs/roborusfacesdk.jar')
	}
	```
* 

* API Initialize
	* In your MainActivity.java OnCreate
	```java
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	...
    	    RoborusFaceClient.initWithApiKey(this, YOUR_API_KEY);
    	}
	```

* Face Age, Gender, Emotion Detect
	* Request
	```java
		private void detect() {
			final Bitmap bitmap = ((BitmapDrawable)imageViewFace.getDrawable()).getBitmap();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

			RoborusFaceClient.getInstance().detect(inputStream, true, true, true, new RoborusFaceClient.DetectCallback() {
				@Override
				public void onDetect(FaceResult faceResult) {
					Log("detect success: " + faceResult.faces.size());

					Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
					myPaint.setColor(0x00FF00);

					textViewResult.setText(faceResult.toJSON());
				}
				@Override
				public void onError(String error) {
					Log("detect error : " + error);
				}
			});
		}
	```
	* Response : 
		* Single face detect
		```json
		{
			"faces": [
				{
					"age": 45,
					"emotion": "neutral",
					"gender": "M",
					"x1": 180,
					"x2": 367,
					"y1": 118,
					"y2": 305
				}
			],
			"height": 426,
			"width": 640
		}
		```
		* Multi face detect
		```json
		{
			"faces": [
				{
					"age": 52,
					"emotion": "sad",
					"gender": "M",
					"x1": 127,
					"x2": 236,
					"y1": 283,
					"y2": 391
				},
				{
					"age": 51,
					"emotion": "neutral",
					"gender": "M",
					"x1": 187,
					"x2": 295,
					"y1": 68,
					"y2": 176
				}
			],
			"height": 640,
			"width": 425
		}
		```