# triplej-hitlist-app

## What is this?
This is an Android app I attempted to make that would import the music hitlist created by the Australian radio station Triple J. You can see the current hitlist here: http://www.abc.net.au/triplej/music/hit_list.htm. Fortunately, the list is exposed as a JSON list via a simple REST API. 

However, the idea was to not only simply display this list. Being in a different country meant I didn't always get to listen to Triple J during their normal daytime hours. Because of this, I felt I didn't get to hear all the music that was currently getting the most play time during the day (the mid-dawn show is pretty good at it I must admit though). The benefit of this application was to allow you to open a track from the list in a localised music playing application on your device. This worked by using Android's native Intent support for searching for media ([INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH](http://developer.android.com/reference/android/provider/MediaStore.html#INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)). To get a better idea, take a look at the class HitListCardQueryListener.java. On the trigger of the Intent, the Android OS would ask the user which local application the search should attempt the open the result in. Local testing proved this worked well with YouTube and the Google Music applications. I assume other applications (e.g. Spotify) would work just as well.

**In short, the software bypassed having to manually search and play for the most popular music from Triple J.**

![screenshot](https://github.com/kdrakon/triplej-hitlist-app/blob/master/screenshot.png)

## What's next?
As usual, other projects popped up and I shelved this one. However, please feel free to take this idea and/or the code to complete the application or make your own variation of it.
