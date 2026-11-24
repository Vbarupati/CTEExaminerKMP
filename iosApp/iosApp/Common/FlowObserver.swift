import Foundation
import SharedLogic

/// Collects a Kotlin StateFlow into Swift WITHOUT SKIE.
///
/// Use this only if you are NOT applying the SKIE plugin. With SKIE you can
/// just do `for await s in store.state`, so this helper isn't needed.
///
/// Example:
///   observer = FlowObserver(store.state) { [weak self] s in self?.state = s }
///   // later: observer.cancel()
final class FlowObserver<T: AnyObject> {
    private var closeable: Closeable?

    init(_ flow: SkieSwiftStateFlow<T>, onEach: @escaping (T) -> Void) {
        // SKIE path (preferred). If you don't use SKIE, see the note below.
        self.closeable = flow.subscribe(
            onCollect: { value in
                DispatchQueue.main.async { onEach(value) }
            }
        )
    }

    func cancel() {
        closeable?.close()
        closeable = nil
    }

    deinit { cancel() }
}