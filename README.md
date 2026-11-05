# Khula - Skills Exchange Platform

Khula is an Android mobile application designed to connect local skilled individuals with customers who need practical services. The name Khula comes from the isiZulu word meaning "to grow". The platform aims to help people turn their skills into income, reduce local unemployment, and build stronger community trust.

Khula is built around three main values:

* **Unity**: Bringing skilled service providers and customers together on one platform.
* **Growth**: Giving individuals a way to earn money and expand their micro-businesses.
* **Support**: Creating a safe space for communities to connect through verified accounts and ratings.

---

## User Roles

The application supports two primary user roles:

1. **Customer**: Users who search for local service providers, send booking requests, chat with providers, track booking history, save favourite providers, and leave ratings and reviews.

2. **Provider**: Skilled individuals (e.g., locticians, plumbers, tutors, graphic designers) who create listings, manage their service catalog, set pricing, manage booking requests, track business analytics, and build their reputation through customer feedback.

The application uses role-based access control to ensure that customers and providers can only access features and information appropriate to their roles.

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
2. Once the internet connection is restored, the mobile app automatically syncs the offline actions with the ASP.NET Core REST API hosted on Render.
3. The API validates the requests and updates the main PostgreSQL database on Supabase.
4. Successfully synchronised local records are marked as synchronised.

---

## Application Screens and UI Design

### 1. Animated Splash Screen

* **Purpose**: Serves as the initial launch screen when the application opens. It displays an animated branding sequence featuring the Khula logo and core values before transitioning directly to authentication.

### 2. Step-Based Registration Screen

* **Purpose**: Allows new users to create an account as either a Customer or a Provider.
* **UI Design Choice (Why Step-Based?)**: Registration is split into 3 clear steps (Personal Details -> Location -> Account Type) instead of one long form.
  * **Reason**: This reduces **cognitive load** on the user. When users see many input fields on a single screen, they can feel overwhelmed and abandon the sign-up process. Breaking the form into small, bite-sized steps makes registration faster, easier to complete, and less mentally taxing.
* **Registration Details**: Users provide their full name, email address, phone number, province, city, suburb, and password.

### 3. Login Screen

* **Purpose**: Secures account access using email/password authentication or Google Single Sign-On (SSO). Passwords are securely hashed before being stored.
* **Additional Features**:
  * Email verification to help confirm user accounts.
  * Password recovery through email verification.
  * Secure session management.

### 4. Customer Home (Dashboard)

* **Purpose**: The central discovery view for customers.
* **Key Elements**: Search bar, popular category icons (Hair & Beauty, Tutoring, Home Services, Repairs, Design & Tech), recommended provider cards, and current regional location indicator.
* **Bottom Navigation**: Offers quick switching between Home, Bookings, Messages, Favourites, and Profile.

### 5. Search Results Screen

* **Purpose**: Displays providers matching a specific search or category query.
* **Key Elements**: Search field, filter button, provider cards with star ratings, region tags, and base starting prices.
* **Region-First Priority**: Search algorithms prioritize providers operating in the exact suburb first, then surrounding suburbs, followed by the broader city/province.
* **Search Filters**: Customers can filter search results by:
  * Skill or service category.
  * Province.
  * City.
  * Suburb or region.
  * Customer rating.
  * Price range.
  * Service availability.

### 6. Provider Profile

* **Purpose**: Presents detailed information about a selected service provider.
* **Key Elements**: Profile header, star rating, completed job count, bio ("About"), interactive service and pricing list, portfolio image gallery, and direct action buttons ("Message" and "Request Service").
* **Additional Information**: Provider profiles can include a profile picture, skills offered, years of experience, general service location, availability, and previous work.

### 7. Request Service Screen

* **Purpose**: Allows a customer to submit a structured booking request to a provider.
* **Key Elements**: Service dropdown selector, preferred date picker, preferred time picker, and an optional description field.

### 8. Message / Chat Screen

* **Purpose**: Facilitates real-time communication between customers and providers regarding service details.
* **Privacy Feature**: Exact residential addresses are strictly hidden initially. Once a provider accepts a request, they can securely share their exact location within the chat view, featuring an "Open in Maps" action button.

### 9. Provider Dashboard

* **Purpose**: The primary overview hub for service providers.
* **Key Elements**: Analytics overview cards (New Requests, Upcoming, Completed, Average Rating), recent pending requests list, and provider bottom navigation (Dashboard, Services, Bookings, Messages, Profile).

### 10. My Services Screen (Provider)

* **Purpose**: Allows service providers to manage their offered service offerings.
* **Key Elements**: Service list cards showing price and duration, availability toggle switches, and an option to add new service listings.
* **Service Listing Information**: Each listing can include a service title, description, category, estimated price, estimated completion time, availability status, and relevant portfolio images.

### 11. Booking History Screen

* **Purpose**: Displays all user bookings and their real-time statuses.
* **Key Elements**: Filter tabs (All, Pending, Incoming, Completed, Cancelled), booking card items with provider details, date, time, status badges, and pricing.
* **Booking Statuses**: Bookings can move through the following statuses:
  * Pending
  * Accepted
  * In Progress
  * Completed
  * Cancelled

Providers can accept or decline incoming booking requests.

### 12. Reviews Screen

* **Purpose**: Allows users to view feedback, average ratings, and written reviews to build platform trust.
* **Key Elements**: Overall rating score, 5-star distribution progress bars, and individual customer review items.
* **Review Requirements**: Customers can submit a rating from one to five stars and write a review after a booking has been marked as completed. This helps ensure that feedback comes from actual service experiences.

### 13. Settings Screen

* **Purpose**: Provides general app configuration options.
* **Key Elements**: Account information, password change, language selector (supporting Setswana and isiZulu), push notification controls, dark mode toggle, privacy policy links, and logout option.
* **Additional Settings**: Users can manage profile visibility and request account deletion.

---

## Additional Application Features

### 14. Favourite Providers

Customers can save providers to a personal favourites list.

This feature allows customers to quickly access providers they have previously used or are interested in contacting without having to search for them again.

Favourite providers can be managed through the Favourites section of the customer navigation menu.

### 15. Growth Badges

Khula includes a gamification feature called **Growth Badges**, designed to encourage providers to build a positive reputation and grow their businesses.

Providers can earn badges based on their activity and achievements on the platform.

Examples include:

* **New Provider**: Awarded after completing the first service.
* **Trusted Provider**: Awarded for maintaining strong customer ratings.
* **Reliable Provider**: Awarded for consistently completing accepted bookings.
* **Community Favourite**: Awarded for frequently being selected by customers in their region.

The purpose of Growth Badges is to recognise positive provider activity, encourage reliability, and motivate individuals to continue improving their services.

### 16. Trust and Safety

Khula includes features designed to create a safer and more trustworthy environment for customers and providers.

These include:

* **Report Users**: Users can report inappropriate behaviour, suspicious activity, or problematic accounts.
* **Block Users**: Users can block other users from contacting them.
* **Provider Verification**: Email and phone number verification help strengthen the reliability of provider accounts.
* **Verification Indicators**: Verified providers may display a verification indicator on their profiles.
* **Privacy Protection**: Exact provider addresses remain hidden until the appropriate stage of the booking process.

These features encourage responsible behaviour and help users make more informed decisions when connecting through the platform.

### 17. Location and Privacy Management

Khula uses region-based searching to help customers discover service providers within their communities.

During registration, users provide their province, city, and suburb. With user permission, the application can also use the device's GPS location to improve search results.

Search results follow this general priority:

1. Providers in the same suburb or region.
2. Providers in nearby suburbs.
3. Providers in other areas of the same city.
4. Providers in other areas of the province.

Providers' exact residential addresses are not publicly displayed. Customers initially see only their general service region.

After a provider accepts a booking request, they may share their exact location through the in-app messaging feature when necessary.

### 18. Offline Mode and Synchronisation

Khula provides limited offline functionality through a local Room Database (SQLite).

When there is no internet connection, users can:

* View previously loaded provider information.
* Access previously loaded booking information.
* Add providers to favourites.
* Create draft booking requests.
* Update selected profile information.

Actions performed offline are stored locally and marked as pending synchronisation.

Once internet connectivity is restored, the application automatically attempts to synchronise pending changes with the REST API.

Successfully synchronised records are updated locally to reflect their synchronisation status.

This allows users to continue using selected features even when an internet connection is temporarily unavailable.

### 19. Push Notifications

Khula uses Firebase Cloud Messaging (FCM) to deliver important notifications to users.

Notifications include:

* New booking requests.
* Booking acceptance or rejection.
* Booking cancellations.
* New messages.
* Reviews received.
* Important account updates.

Users can manage their notification preferences through the Settings section.

### 20. Multi-Language Support

Khula supports South African languages to improve accessibility and create a more familiar experience for local communities.

The application supports:

* Setswana.
* isiZulu.

Users can select their preferred language during setup or through the Settings section.

The selected language applies to supported user-interface elements, including navigation labels, buttons, messages, error messages, notifications, and other application text.

---

## REST API and Database Communication

Khula communicates with a custom ASP.NET Core REST API that connects the Android application to the online PostgreSQL database hosted on Supabase.

The REST API manages application information, including:

* User accounts and authentication.
* Provider profiles.
* Services and service listings.
* Bookings.
* Messages.
* Reviews and ratings.
* Notifications.
* Favourite providers.

The Android application communicates with the API using HTTP requests and JSON data.

HTTPS is used to protect information transmitted between the mobile application and the backend API.

Entity Framework Core manages database operations, allowing authorised users to create, retrieve, update, and delete appropriate application records.

Input validation and role-based permissions help ensure that requests are processed securely and that users only access authorised resources.

---

## Security and Privacy (POPIA Compliance)

Khula prioritises the security and privacy of user information.

The application follows the principles of the Protection of Personal Information Act (POPIA) by limiting unnecessary collection and exposure of personal information.

Security measures include:

* **Data Protection**: Limits unnecessary collection and exposure of personal data.
* **Password Security**: Passwords are securely hashed before storage in Supabase PostgreSQL.
* **Secure Authentication**: Supports email/password authentication and Google Single Sign-On.
* **Email Verification**: Helps confirm user accounts and supports password recovery.
* **Input Validation**: Validates user input to reduce invalid or unsafe data submissions.
* **Session Management**: Helps protect authenticated user sessions.
* **Role-Based Access Control**: Restricts access to functionality based on user roles.
* **HTTPS Communication**: Protects information transferred between the Android application and REST API.
* **Controlled Location Sharing**: Exact addresses are suppressed until a booking request is accepted by the provider.
* **Account Privacy**: Provides profile visibility controls and account deletion requests.

These measures help create a safer environment for customers and service providers.

---

## YouTube Video

Watch the Khula application presentation and demonstration:

[Watch Khula on YouTube](https://youtu.be/cCXpT8ZU8iU)

## Khula API GitHub Repository

[View the Khula REST API Repository](https://github.com/thokozani2005/KhulaRESTAPI.git)

---

## Project Vision

Khula aims to make practical skills more visible, accessible, and valuable within local communities.

By connecting skilled individuals with customers, the platform creates opportunities for people to earn an income, build their reputation, and grow their small businesses.

**Khula – Talent is everywhere. Opportunity should be too.**

**Unity. Growth. Support.**
