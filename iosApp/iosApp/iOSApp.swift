import SwiftUI
import BackgroundTasks
import UserNotifications
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, _ in
            print(granted ? "✅ Notification Permission Granted" : "❌ Denied")
        }

        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: "com.webscare.interiorismai.imageProcessing",
            using: nil
        ) { task in
            self.handleImageProcessingTask(task: task as! BGProcessingTask)
        }
        return true
    }

    func handleImageProcessingTask(task: BGProcessingTask) {
        print("🚀 BGProcessingTask started!")

        // Pending fetchUrls UserDefaults se lo
        let fetchUrls = UserDefaults.standard.stringArray(forKey: "PENDING_FETCH_URLS") ?? []
        print("📋 Fetch URLs: \(fetchUrls)")

        if fetchUrls.isEmpty {
            task.setTaskCompleted(success: true)
            return
        }

        task.expirationHandler = {
            print("⚠️ BGProcessingTask expired!")
            task.setTaskCompleted(success: false)
        }

        // Kotlin ImageCheckHelper call karo
        let helper = ImageCheckHelper()
        helper.checkAndNotify(fetchUrls: fetchUrls) {
            UserDefaults.standard.removeObject(forKey: "PENDING_FETCH_URLS")
            task.setTaskCompleted(success: true)
            print("✅ BGProcessingTask completed!")
        }
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        KoinHelper().doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(.all)
        }
    }
}
