# ECE452 (MunchBox)

## Architecture

Frontend
- Using [Android Jetpack Compose](https://developer.android.com/jetpack/compose?gclid=CjwKCAjwv8qkBhAnEiwAkY-ahgFeNJuKAGs3iRiekokk1UBmIqxxX9nO7lrz8RWzOK99RhlRu0TBuRoCrksQAvD_BwE&gclsrc=aw.ds)
- Using [Compose Material 3](https://developer.android.com/jetpack/androidx/releases/compose-material3)



## File System
While viewing in the "Android" view in Android Studio 
>     Munchbox/...
> 
>        └── Java/...
> 
>           └── controller/...
>
>           └── data/...
>               └── FakeDataSource.kt
>                    - has fake data until / if we get restaurants irl
>
>           └── ui/...
>                - store any FrontEnd stuff in here
>
>           └── MainActivity.kt
>                - The starting point for the app
>
>           └── MainScreen.kt
>                - Where any navigation logic goes, ran by MainActivity
>
>        └── res/values/...
>            - Single Source of Truth, any global value are stored in these XMLs
>            └── colors.xml
>            └── dimens.xml
>            └── strings.xml
>               - all client-facing text goes here 
>               - imported as R in a lot of files
>            └── themes.xml
>
>        └── build.gradle (project)
>           - has general info for the entire project (android & kotlin versions)
>
>        └── build.gradle (app)
>            - has general info for the APP (dependencies, build features, etc.)
>




## Firebase

After downloading the `google-services.json` add it to your **(app-level)** root directory. This file is basically our API key for using Firebase services.  
**Example:**  
>     Munchbox/...
> 
>        └── App/...
> 
>           └── Src/...
> 
>           └── google-services.json <--(put it here)
>
>           └── ...


