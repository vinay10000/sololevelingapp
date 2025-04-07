# Hunter's Ascension - Solo Leveling-Inspired Fitness App

A gamified fitness tracking Android app inspired by the "Solo Leveling" anime/manhwa, designed to transform your fitness journey into an RPG adventure.

## Features

- **RPG Progression System**: Gain experience points, level up, and unlock new abilities
- **Rank System**: Progress through ranks from E to S with unique benefits at each level
- **Stat System**: Build your character's STR, AGI, VIT, INT, and LUK through various exercises
- **Workout Tracking**: Log exercises, track stats, and record progress
- **Trophy System**: Earn achievements and trophies for workout streaks and milestones

## Getting Started

### Prerequisites

- Android Studio 4.2 or higher
- JDK 8 or higher
- Android device or emulator running Android 5.0 (API 21) or higher

### Building the App

1. Clone this repository to your local machine
2. Open the project in Android Studio
3. Sync the Gradle files
4. Build the project using the "Build" menu
5. Run the app on your device or emulator

### Using the Replit Environment

1. Make sure you have the Gradle wrapper file configured correctly
2. Run the build using the workflow: `./gradlew assembleDebug`
3. The resulting APK will be in `app/build/outputs/apk/debug/app-debug.apk`

### Installing on Your Device

1. Enable "Install from Unknown Sources" in your device settings
2. Copy the APK file to your device
3. Open the APK file on your device to install

## Project Structure

- **UI Layer**: Main activity and fragments for different screens (home, workouts, stats, profile)
- **Data Layer**: Room database with entities for User, Workout, and Trophy
- **Business Logic**: ViewModels and Repositories

## Phase 1 MVP Features

- User authentication (registration and login)
- Basic workout tracking
- Core stat system and progression
- Rank progression mechanics

## Roadmap

- **Phase 2**: Social features, leaderboards, and trophy system
- **Phase 3**: Advanced customization, wearable integration
- **Phase 4**: AR features with monster battles through physical activity

## Screenshots

(Coming soon)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Inspired by "Solo Leveling" by Chugong
- Thanks to all fitness enthusiasts who provided feedback
