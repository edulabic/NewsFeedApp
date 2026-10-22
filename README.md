# NewsFeedApp

An Android news reader built with **Kotlin** and **Jetpack Compose**. It shows a list of news articles, lets you filter them by category, date range and unwanted words, fetches live news and image tags from web services, and stores everything locally with **Room** so the app keeps working offline.
Developed as a course project for the Mobile App Development course at Faculty of Electrical Engineering Sarajevo.

## Features

- **News feed** with a smooth, lazily loaded list and two card styles (standard and featured)
- **Category filters** for Politics, Sport, Science/Technology, and more
- **Advanced filters**: date range picker and a list of unwanted words to hide from your feed
- **News details** with the full article info, image, image tags, and related stories
- **Live news** fetched from a web service, with new stories highlighted at the top
- **Offline support**: everything you've seen is saved locally, so the app still works without an internet connection

## Built with

- Kotlin
- Jetpack Compose and Material 3
- Navigation Compose
- Room (local database)
- Retrofit and Kotlin Coroutines
- Coil (image loading)

## Getting started

1. Clone the repository
   ```bash
   git clone https://github.com/<your-username>/<your-repo>.git
   ```
2. Add your API keys to `local.properties`
   ```properties
   NEWS_API_KEY=your_news_api_key
   IMAGE_TAGGING_API_KEY=your_image_tagging_api_key
   ```
3. Open the project in Android Studio, sync Gradle, and run it on a device or emulator.

## Author

Esma Đulabić

## License

This project was created for educational purposes.
