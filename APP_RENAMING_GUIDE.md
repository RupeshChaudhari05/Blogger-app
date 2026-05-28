# App Renaming Guide - For Play Store Publishing

**Current App Name:** Quotes  
**Current Project Name:** Waller  
**Current Package Name:** com.blogger.wallpaper

---

## 🎯 All "Waller" References Found (25 total)

### 1. **Project-Level Names**

| Item | Location | Current Value | Should Change? |
|------|----------|---------------|----------------|
| Project Name | `settings.gradle` | `Waller` | ✅ YES (internal) |
| IDE Name | `.idea/.name` | `Waller` | ✅ YES (auto-synced) |

---

### 2. **App Display Name (User-Facing)**

| Location | Current Value | Category |
|----------|---------------|----------|
| `res/values/strings.xml` | `Quotes` | 🎯 **PRIMARY APP NAME** |
| `strings.xml` tagline | `Blogger Quotes App` | App tagline |

**This is what users see in Play Store and on their device!**

---

### 3. **Theme Styling (12 files)**

These are internal naming - users don't see these:

```
Theme Names:
- Theme.Waller (main theme in values/themes.xml and values-night/themes.xml)
- Theme.Waller.Translucent (translucent dialog)
- Theme.Waller.Dialog (dialog theme)
- Theme.Waller.AppWidgetContainerParent (widget theme)
- Theme.Waller.AppWidgetContainer (widget container)

Widget Styles:
- Widget.Waller.AppWidget.Container
- Widget.Waller.AppWidget.InnerView
```

**Files containing themes:**
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values/styles.xml`
- `app/src/main/res/values-night/themes.xml`
- `app/src/main/res/values-v21/styles.xml`
- `app/src/main/res/values-v31/styles.xml`
- `app/src/main/res/values-v31/themes.xml`
- `app/src/main/res/values-night-v31/themes.xml`
- `app/src/main/AndroidManifest.xml` (3 references)
- `app/src/main/res/layout/top_widget.xml` (2 references)

---

### 4. **File Download Directory Settings**

| Item | Location | Current Value | Purpose |
|------|----------|---------------|---------|
| Download Directory | `AppConfig.java` line 32 | `"Waller"` | Where user-saved files go |
| File Prefix | `AppConfig.java` line 31 | `"waller_"` | Prefix for saved image files |

**Visible to users** when they access `Android/media/` on their device

---

### 5. **Package & Application ID (CRITICAL)**

| Item | Location | Current Value | Impact |
|------|----------|---------------|--------|
| Package Name | `build.gradle` | `com.blogger.wallpaper` | ⚠️ **CANNOT CHANGE** (signing key tied to this) |
| Namespace | `build.gradle` | `com.blogger.wallpaper` | ⚠️ **CANNOT CHANGE** (unique identifier) |
| Application ID | `build.gradle` | `com.blogger.wallpaper` | ⚠️ **CANNOT CHANGE** (Play Store ID) |

**Why?** 
- The signing key is bound to this package name
- Changing it means it's a different app (old users won't get updates)
- You'd need a new signing key and lose all app history

---

## 📋 Renaming Checklist

### ✅ MUST Change (User-Facing)
```
PRIORITY: CRITICAL - Will be visible to users

☐ App Display Name (strings.xml)
  From: "Quotes"
  To: [YOUR NEW NAME]
  
☐ Download Directory (AppConfig.java)
  From: "Waller"
  To: [YOUR NEW NAME]
  
☐ File Prefix (AppConfig.java)
  From: "waller_"
  To: [SHORTER VERSION]
```

### ⚠️ OPTIONAL (Internal, But Recommended)
```
PRIORITY: HIGH - For code cleanliness

☐ Project Name (settings.gradle)
  From: "Waller"
  To: [YOUR NEW NAME]
  
☐ All Theme Names
  From: "Theme.Waller" → "Theme.YOUR_NAME"
  From: "Widget.Waller" → "Widget.YOUR_NAME"
  (15 replacements in XML files)
```

### ❌ CANNOT Change (Technical Limitation)
```
DO NOT CHANGE - Will break app:

✗ Package Name: com.blogger.wallpaper
✗ Application ID: com.blogger.wallpaper
✗ Namespace: com.blogger.wallpaper

These are permanently tied to your signing key.
Changing them creates a new app, not an update.
```

---

## 🔄 Examples of Renaming

### Example 1: Renaming to "InspireMe"

```
BEFORE:
- App Name: Quotes
- Project: Waller
- Theme: Theme.Waller
- Download Dir: Waller
- File Prefix: waller_

AFTER:
- App Name: InspireMe
- Project: InspireMe
- Theme: Theme.InspireMe
- Download Dir: InspireMe
- File Prefix: inspire_
```

### Example 2: Renaming to "DailyQuotes"

```
BEFORE:
- App Name: Quotes
- Project: Waller
- Theme: Theme.Waller
- Download Dir: Waller
- File Prefix: waller_

AFTER:
- App Name: DailyQuotes
- Project: DailyQuotes
- Theme: Theme.DailyQuotes
- Download Dir: DailyQuotes
- File Prefix: daily_
```

---

## 🛠️ How to Rename

### Step 1: Decide on New Name
What will you call your app on the Play Store?

New Name: `_________________`

### Step 2: Change App Display Name
Edit: `app/src/main/res/values/strings.xml`

```xml
<!-- BEFORE -->
<string name="app_name">Quotes</string>

<!-- AFTER -->
<string name="app_name">YOUR_NEW_NAME</string>
```

### Step 3: Change Download Directory
Edit: `app/src/main/java/com/blogger/wallpaper/AppConfig.java`

```java
// BEFORE
public String download_directory = "Waller";

// AFTER
public String download_directory = "YOUR_NEW_NAME";
```

### Step 4: Change File Prefix
Edit: `app/src/main/java/com/blogger/wallpaper/AppConfig.java`

```java
// BEFORE
public String prefix_filename = "waller_";

// AFTER
public String prefix_filename = "your_prefix_";
```

### Step 5: (Optional) Change Project Name
Edit: `settings.gradle`

```gradle
// BEFORE
rootProject.name = "Waller"

// AFTER
rootProject.name = "YOUR_NEW_NAME"
```

### Step 6: (Optional) Rename All Themes
Use Find & Replace in Android Studio:

```
Find:    Theme.Waller
Replace: Theme.YOUR_ACRONYM

Find:    Widget.Waller
Replace: Widget.YOUR_ACRONYM
```

Files to update:
- `values/themes.xml`
- `values-night/themes.xml`
- `values/styles.xml`
- `values-v21/styles.xml`
- `values-v31/styles.xml`
- `values-v31/themes.xml`
- `values-night-v31/themes.xml`
- `AndroidManifest.xml`
- `layout/top_widget.xml`

---

## 📍 File Locations - Quick Reference

### Essential Changes (Do These!)

1. **App Display Name**
   ```
   📄 app/src/main/res/values/strings.xml
   Line: <string name="app_name">
   ```

2. **Download Directory**
   ```
   📄 app/src/main/java/com/blogger/wallpaper/AppConfig.java
   Line: public String download_directory = "Waller";
   ```

3. **File Prefix**
   ```
   📄 app/src/main/java/com/blogger/wallpaper/AppConfig.java
   Line: public String prefix_filename = "waller_";
   ```

### Optional Changes (For Cleanliness)

4. **Project Name**
   ```
   📄 settings.gradle
   Line: rootProject.name = "Waller"
   ```

5. **Theme Names** (15 occurrences)
   ```
   📄 app/src/main/res/values/themes.xml
   📄 app/src/main/res/values-night/themes.xml
   📄 app/src/main/res/values-v31/themes.xml
   📄 app/src/main/res/values-v31/styles.xml
   📄 app/src/main/AndroidManifest.xml
   📄 app/src/main/res/layout/top_widget.xml
   (and more)
   ```

---

## 🚨 Important Notes

### What Users Will See

✅ **Changes visible to users:**
- App name on Play Store: "Quotes" → "YOUR_NAME"
- App icon label on home screen: "Quotes" → "YOUR_NAME"
- Downloaded images folder: `/Waller/` → `/YOUR_NAME/`
- Saved image files: `waller_123.jpg` → `yourprefix_123.jpg`

### What You CAN'T Change

❌ **These are permanent:**
- Package name: `com.blogger.wallpaper` (tied to signing key)
- Application ID: `com.blogger.wallpaper` (tied to signing key)
- In code, the package will always be `com.blogger.wallpaper`

### What Only Developers See

⚠️ **Internal changes (not visible to users):**
- Theme names: `Theme.Waller` → `Theme.YourApp`
- Project name in IDE: `Waller` → `YourApp`
- These are just for code organization

---

## 📊 Impact Summary

| Change | Impact | Difficulty | Users Affected |
|--------|--------|-----------|-----------------|
| App display name | HIGH | ✅ Easy | ✅ YES |
| Download directory | HIGH | ✅ Easy | ✅ YES |
| File prefix | HIGH | ✅ Easy | ✅ YES |
| Project name | LOW | ✅ Easy | ❌ NO |
| Theme names | NONE | ⚠️ Medium | ❌ NO |
| Package name | CRITICAL | ❌ IMPOSSIBLE | ✅ Would break app |

---

## ✨ Best Practices

### Good Naming
- **Short** (easier to remember and type)
- **Descriptive** (tells users what it does)
- **Unique** (distinguishable on Play Store)
- **Professional** (no numbers, no symbols)

### Examples
- ✅ "InspireDaily" (good)
- ✅ "MotivateMe" (good)
- ✅ "WisdomHub" (good)
- ❌ "Quote2" (too generic)
- ❌ "Waller123" (too technical)

---

## 🎬 Quick Start

**Want to rename to "InspireMe"?**

Replace these 3 values:

1. In `strings.xml`: Change `Quotes` → `InspireMe`
2. In `AppConfig.java`: Change `Waller` → `InspireMe`
3. In `AppConfig.java`: Change `waller_` → `inspire_`

That's it for minimal changes!

---

## 📝 Summary

**Before Publishing to Play Store:**

1. ✅ **Choose your new app name**
2. ✅ **Update 3 files** (display name, download directory, file prefix)
3. ✅ **Optionally rename themes** for code cleanliness
4. ✅ **Do NOT change package name** (impossible and breaks app)
5. ✅ **Build and test**
6. ✅ **Upload to Play Store**

**Time needed:** 5-10 minutes for essential changes

---

**Ready to proceed with renaming? Tell me your new app name and I'll apply all the changes!**
