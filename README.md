# blank-android-gradle
Author: Daniel Passos (dpassos@redhat.com)   
Level: Beginner   
Technologies: Java, Android, RHMAP   
Summary: A template application which Initializes FeedHenry in a Fragment   
Community Project : [Feed Henry](http://feedhenry.org)   
Target Product: RHMAP   
Product Versions: RHMAP 3.8.0+   
Source: https://github.com/feedhenry-templates/blank-android-gradle   
Prerequisites: fh-android-sdk : 3.0.+, Android Studio : 1.4.0 or newer, Android SDK : 22+ or newer   

## What is it?

This application provides a starting point for developing RHMAP applications.  The FeedHenry SDK is initialized in the class `app/src/main/java/org/feedhenry/blank/InitFragment.java`.

If you do not have access to a RHMAP instance, you can sign up for a free instance at [https://openshift.feedhenry.com/](https://openshift.feedhenry.com/).

## How do I run it?

### RHMAP Studio

This application and its cloud services are available as a project template in RHMAP.

### Local Clone (ideal for Open Source Development)
If you wish to contribute to this template, the following information may be helpful; otherwise, RHMAP and its build facilities are the preferred solution.

###  Prerequisites
 * fh-android-sdk : 3.0.+
 * Android Studio : 1.4.0 or newer
 * Android SDK : 16+

## Build instructions
 * Edit `app/src/main/assets/fhconfig.properties` to include the relevant information from RHMAP.
 * Attach running Android Device with API 16+ running
 * ./gradlew installDebug

## How does it work?

### Initialization

The FeedHenry SDK is initialized in `app/src/main/java/org/feedhenry/blank/InitFragment.java`.

