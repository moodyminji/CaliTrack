
# CaliTrack 📱🩺
CaliTrack is an AI-powered health assistant app for Android. It helps users track calories, macronutrients, and exercise through natural language input. By combining Google Gemini for conversational parsing, Nutritionix for nutritional data, and Firebase for authentication and cloud storage, CaliTrack delivers a personalized and modern health tracking experience.

---

## ✨ Features
- Secure **user authentication and profile management**
- **AI-powered food and exercise logging** using natural language
- **Real-time calorie and macronutrient tracking**
- **Personalized calorie goal calculation** based on user metrics
- **Daily activity history** with progress visualization
- **Cloud synchronization** via Firebase Firestore
- **Material You design** for a clean, responsive UI
- Guided **onboarding flow** with personalized setup

---

## 🛠 Tech Stack
**Frontend**
- Android SDK (API Level 24+)
- Java
- Material Design 3 components
- Constraint Layout & Fragment Navigation

**Backend**
- Firebase Authentication (OAuth 2.0)
- Cloud Firestore (NoSQL database)
- Google Gemini API (NLP for food/exercise parsing)
- Nutritionix API (calorie & macro data)

**Build System**
- Gradle with Kotlin DSL
- Multi-module architecture

---

## 📐 Architecture
- **MVVM-inspired pattern**: separates UI and business logic
- **Repository pattern**: consistent data management
- **Firebase**: remote data source
- **SharedPreferences**: local caching

---

## 🔒 Security
- API keys stored in `local.properties` (excluded from version control)
- Firebase security rules for controlled data access
- Email verification for new accounts
- Password strength enforcement (≥ 6 characters)

---

## ⚙️ Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/moodyminji/calitrack.git
   ```
2. Open in **Android Studio**.
3. Add API keys:
   - Gemini API key → `local.properties`
   - Nutritionix App ID & Key → `local.properties`
   - Firebase config → `google-services.json`
4. Sync Gradle and run on emulator or device.

---

## 🧪 Testing
- **Unit tests**: authentication, AI logging, calorie goal calculation, UI updates  
- **Integration tests**: Firebase persistence, Gemini + Firestore end-to-end flow  
- ✅ All MVP test cases passed successfully

---

## 🚧 Future Enhancements
- Barcode scanning for packaged foods  
- Photo-based food recognition  
- Social sharing features  
- Meal planning and recipes  
- Wearable device integration  
- Water intake tracking  
- Sleep monitoring  

---

## 👨‍💻 Author
Developed by **Mohammed** for Mobile Application Development Course.  
Focused on building scalable, accessible, and user-friendly health technology solutions.

---

## 📜 License
Licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
```


