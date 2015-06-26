# Face Detection and Recognition on Google Glass #
This application enable user to detect faces (real-time) and create their own database for face recognition. It uses OpenCV for Android and JavaCV.

## Setup (copied from *https://github.com/space150/google-glass-playground/tree/master/OpenCVFaceDetection*) ##
1. Install and configure the OpenCV4Android SDK. Thorough instructions can be found in the OpenCV4Android SDK tutorial.
2. Google Glass does not have the Google Play store installed, so you will need to manually install the OpenCV Manager apk. Google Glass is running armeabi-v7a, so the OpenCV_x.x.x_Manager_x.x_armv7a-neon.apk manager apk is needed.
3. Update the library reference in this project to point to your OpenCV4Android library.
4. Build and run the project.

## Usage ##
* At normal use application does not detect faces.
* If you tap with three fingers you reach Main Menu. Firstly you can choose preferred resolutions from camera. In Camera Detection you can choose real-time Face Detection with two different classifiers (laggy). In Image Manipulation you can detect faces on images, which are in drawable folder. In Face Detection you choose, which classifier to use when detecting faces and in Face Recognition you can get all names, which are in Face Recognition database via TextToSpeech API or clear database.
* To detect faces you simply say 'Ok Google' and then Detect Faces. You can then predict or train individual face by saying 'Ok Google' or by tap to reach menu.