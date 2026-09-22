# Khula - Skills Exchange Platform

Khula is an Android mobile application designed to connect local skilled individuals with customers who need 
practical services. The name Khula comes from the isiZulu word meaning "to grow". The platform aims to help people 
turn their skills into income, reduce local unemployment, and build stronger community trust.

Khula is built around three main values:
* **Unity**: Bringing skilled service providers and customers together on one platform.
* **Growth**: Giving individuals a way to earn money and expand their micro-businesses.
* **Support**: Creating a safe space for communities to connect through verified accounts and ratings.

---

## User Roles

The application supports two primary user roles:

1. **Customer**: Users who search for local service providers, send booking requests, chat with providers, track
   booking history, and leave ratings and reviews.
3. **Provider**: Skilled individuals (e.g., locticians, plumbers, tutors, graphic designers) who create listings,
   manage their service catalog, set pricing, manage booking requests, and track business analytics.

---

## System Architecture and Backend Stack

The Khula system uses a client-server setup with a mobile frontend, a backend REST API, and cloud databases.

### Backend and Database Technologies
* **Mobile Frontend**: Android Studio using Kotlin.
* **REST API**: ASP.NET Core Web API written in C#. Hosted online using Render.
* **Database**: PostgreSQL hosted on Supabase (migrated from SQL Server). Entity Framework Core handles data access.
* **Offline Storage**: Local Room Database (SQLite) on the Android device for offline viewing and draft actions.
* **Third-Party Integrations**:
  * **Google Sign-In**: Enables Single Sign-On (SSO) authentication.
  * **Google Maps API**: Supports interactive location selection, map search, and distance-based searching.
  * **Firebase Cloud Messaging (FCM)**: Delivers push notifications for new bookings, status updates, and messages.

### How Data Sync Works
1. When offline, actions like adding a favourite provider or creating a draft request are saved locally in the Room Database.
2. Once the internet connection is restored, the mobile app automatically syncs the offline actions with the ASP.NET
   Core REST API hosted on Render.
4. The API validates the requests and updates the main PostgreSQL database on Supabase.

---

## Application Screens and UI Design

### 1. Animated Splash Screen
* **Purpose**: Serves as the initial launch screen when the application opens. It displays an animated branding sequence
* featuring the Khula logo and core values before transitioning directly to authentication.

### 2. Step-Based Registration Screen
* **Purpose**: Allows new users to create an account as either a Customer or a Provider.
* **UI Design Choice (Why Step-Based?)**: Registration is split into 3 clear steps (Personal Details -> Location ->
* Account Type) instead of one long form.
  * **Reason**: This reduces **cognitive load** on the user. When users see many input fields on a single screen,
    they can feel overwhelmed and abandon the sign-up process. Breaking the form into small, bite-sized steps makes
    registration faster, easier to complete, and less mentally taxing.

### 3. Login Screen
* **Purpose**: Secures account access using email/password authentication or Google Single Sign-On (SSO). Passwords are
  encrypted using secure hashing algorithms.

### 4. Customer Home (Dashboard)
* **Purpose**: The central discovery view for customers.
* **Key Elements**: Search bar, popular category icons (Hair & Beauty, Tutoring, Home Services, Repairs, Design & Tech), recommended
* provider cards, and current regional location indicator.
* **Bottom Navigation**: Offers quick switching between Home, Bookings, Messages, Favourites, and Profile.

### 5. Search Results Screen
* **Purpose**: Displays providers matching a specific search or category query.
* **Key Elements**: Search field, filter button, provider cards with star ratings, region tags, and base starting prices.
* **Region-First Priority**: Search algorithms prioritize providers operating in the exact suburb first, then surrounding suburbs,
  followed by the broader city/province.

### 6. Provider Profile
* **Purpose**: Presents detailed information about a selected service provider.
* **Key Elements**: Profile header, star rating, completed job count, bio ("About"), interactive service & pricing list, portfolio
  image gallery, and direct action buttons ("Message" and "Request Service").

### 7. Request Service Screen
* **Purpose**: Allows a customer to submit a structured booking request to a provider.
* **Key Elements**: Service dropdown selector, preferred date picker, preferred time picker, and an optional description field.

### 8. Message / Chat Screen
* **Purpose**: Facilitates real-time communication between customers and providers regarding service details.
* **Privacy Feature**: Exact residential addresses are strictly hidden initially. Once a provider accepts a request, they can securely
  share their exact location within the chat view, featuring an "Open in Maps" action button.

### 9. Provider Dashboard
* **Purpose**: The primary overview hub for service providers.
* **Key Elements**: Analytics overview cards (New Requests, Upcoming, Completed, Average Rating), recent pending requests list, and
  provider bottom navigation (Dashboard, Services, Bookings, Messages, Profile).

### 10. My Services Screen (Provider)
* **Purpose**: Allows service providers to manage their offered service offerings.
* **Key Elements**: Service list cards showing price and duration, availability toggle switches, and an option to add new service listings.

### 11. Booking History Screen
* **Purpose**: Displays all user bookings and their real-time statuses.
* **Key Elements**: Filter tabs (All, Pending, Incoming, Completed, Cancelled), booking card items with provider details, date, time,
  status badges, and pricing.

### 12. Reviews Screen
* **Purpose**: Allows users to view feedback, average ratings, and written reviews to build platform trust.
* **Key Elements**: Overall rating score, 5-star distribution progress bars, and individual customer review items.

### 13. Settings Screen
* **Purpose**: Provides general app configuration options.
* **Key Elements**: Account information, password change, language selector (supporting Setswana and isiZulu),
  push notification controls, dark mode toggle, privacy policy links, and logout option.

---

## Security and Privacy (POPIA Compliance)

* **Data Protection**: Strictly adheres to POPIA principles by minimizing the unnecessary collection or exposure of personal data.
* **Password Security**: Passwords are securely hashed before storage in Supabase PostgreSQL.
* **Controlled Location Sharing**: To protect user privacy and personal safety, exact addresses are suppressed until
  a booking request is officially accepted by the provider.

# You Tube video Link
https://youtu.be/cCXpT8ZU8iU

# Khula API GIT HUB REPO LINK
https://github.com/thokozani2005/KhulaRESTAPI.git

