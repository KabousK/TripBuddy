# TripBuddy (prototype)

This repo contains an Android prototype that implements the TripBuddy brief. It uses a Drawer + Navigation component with modular fragments.

Highlights (mapped to deliverables):

- Deliverable 2
  - Proper package structure under `com.example.formative_eduv4834254` with `data`, `ui`, and `util` packages.
  - Navigation graph in `res/navigation/mobile_navigation.xml` connects Registration → Home → Memories/Gallery/Budget/Settings.
  - Responsive layouts use ConstraintLayout/Material components; colors & themes defined in `res/values`.
  - Visual branding: custom colors, splash via Android 12 API, and a welcome dashboard (`fragment_home.xml`).

- Deliverable 3
  - Trip planning: `BudgetFragment` with destination, notes, date pickers, predefined activities (RecyclerView), custom expenses with validation.
  - Real-time budget: `BudgetCalculator` pure functions; totals update on input/selection.
  - Loyalty: discount 10% when trip count ≥ 3 using `SessionManager` persistence.
  - Summary screen: `BudgetSummaryFragment` formats subtotal/discount/total.

- Deliverable 4
  - Background music with `MediaPlayer` in `MemoriesFragment` and full-screen `MemoryViewerFragment`.
  - Animations: simple fade-in/out in `res/anim` + image fade-in on bind.
  - Dynamic gallery: `GalleryFragment` + `GalleryAdapter` grid; tap opens viewer with optional music.

- Deliverable 5
  - Login/session via `RegistrationFragment` + `SessionManager` (SharedPreferences).
  - Memories persisted in SQLite via `DbHelper`/`MemoryDao` (text, photo, audio, metadata). Editable via dialog.
  - Previous trip selections and trip counter persisted via `SessionManager`.
  - Settings screen persists theme/music/language preferences.

TripBuddy Engagement Score (TES): `util/TESManager.java` applies multipliers for new trip, memory with photo, gallery interaction, and loyalty usage; score is stored in `SessionManager` and surfaced on the Home screen.

Build
- Android Gradle Plugin with Java 11. See `app/build.gradle.kts` for deps. Compile/target SDKs set for preview.

Note: This prototype avoids copying the alternate "Colin formative" code but follows similar principles: modular architecture, Material UI, RecyclerView grids, dialogs, SQLite for structured data, and SharedPreferences for lightweight state.
