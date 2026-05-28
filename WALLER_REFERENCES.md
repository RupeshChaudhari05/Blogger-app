# Waller References Found - Complete Inventory

**Total Occurrences:** 25  
**User-Facing:** 3  
**Internal/Code:** 22

---

## 📱 What Users Will See (MUST CHANGE)

### 1. App Display Name
**File:** `app/src/main/res/values/strings.xml`
```xml
<string name="app_name">Quotes</string>
```
↓ Change this to your new Play Store name

---

### 2. Download Folder Name
**File:** `app/src/main/java/com/blogger/wallpaper/AppConfig.java` (Line 32)
```java
public String download_directory = "Waller";
```
↓ When users save images, they go to `Android/media/Waller/`

---

### 3. Downloaded File Prefix
**File:** `app/src/main/java/com/blogger/wallpaper/AppConfig.java` (Line 31)
```java
public String prefix_filename = "waller_";
```
↓ Downloaded images are named like: `waller_001.jpg`

---

## 🎨 Theme Styling (Internal - Developers Only)

### Files with Theme Names:
1. `values/themes.xml` - 5 occurrences
2. `values-night/themes.xml` - 1 occurrence
3. `values/styles.xml` - 2 occurrences
4. `values-v21/styles.xml` - 2 occurrences
5. `values-v31/styles.xml` - 2 occurrences
6. `values-v31/themes.xml` - 1 occurrence
7. `values-night-v31/themes.xml` - 1 occurrence
8. `AndroidManifest.xml` - 3 occurrences
9. `layout/top_widget.xml` - 2 occurrences

### Theme Names to Rename:
- `Theme.Waller` → `Theme.YourName`
- `Theme.Waller.Translucent` → `Theme.YourName.Translucent`
- `Theme.Waller.Dialog` → `Theme.YourName.Dialog`
- `Widget.Waller.AppWidget.Container` → `Widget.YourName.AppWidget.Container`
- `Widget.Waller.AppWidget.InnerView` → `Widget.YourName.AppWidget.InnerView`

---

## 📊 Project Names (Internal Only)

### 1. Project Name
**File:** `settings.gradle` (Line 18)
```gradle
rootProject.name = "Waller"
```
↓ Only shows in Android Studio IDE

---

### 2. IDE Project Name
**File:** `.idea/.name` (Line 1)
```
Waller
```
↓ Auto-synced with settings.gradle

---

## ⚠️ CANNOT Change (Permanently Locked)

### Package Name & Application ID
```gradle
applicationId "com.blogger.wallpaper"  // ❌ Cannot change
namespace 'com.blogger.wallpaper'       // ❌ Cannot change
```

**Why?** Your signing key is tied to this package name. Changing it creates a completely new app.

---

## 🎯 Renaming Strategy

### Option 1: Minimal Changes (Recommended for Quick Launch)
Only change what users see - **3 files**

```
1. strings.xml - App name
2. AppConfig.java - Download directory
3. AppConfig.java - File prefix
```
⏱️ **Time:** 2 minutes  
✅ **Users Happy:** Yes  
✅ **Code Clean:** Partial

---

### Option 2: Complete Renaming (Best Practice)
Change everything for consistency - **All 12+ files**

```
1. strings.xml - App name
2. AppConfig.java - Download directory & prefix
3. settings.gradle - Project name
4. All theme files - Update theme names
5. AndroidManifest.xml - Update theme references
6. Layout files - Update theme references
```
⏱️ **Time:** 15 minutes  
✅ **Users Happy:** Yes  
✅ **Code Clean:** Yes (perfect)

---

## 📋 Your New App Name

**What would you like to call your app?**

Current: "Quotes"  
Proposed: **___________________**

---

## 🚀 Ready to Apply?

Tell me your new app name and which option you prefer:

**Option 1:** Just change the user-facing parts (3 files)  
**Option 2:** Complete rename everything (all files)

Examples:
- "Tell me to rename to 'InspireMe' with option 1"
- "Rename to 'DailyWisdom' with option 2"

I'll apply all changes automatically!
