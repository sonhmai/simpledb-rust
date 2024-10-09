
/// BufferManager (BM) is responsible for managing user data files.
/// It needs to support arbitrary access to data files.
///
/// How?
/// - it allocates a fixed set of pages, called `buffer pool` which should fit into memory
/// and the pages should come from OS I/O buffer.
/// - client talks to `BufferManager` to access a block (page) following protocol
///     1. client asks buffer manager to `pin` a page from buffer pool to that block.
///     2. client accesses contents of pages as much as it wants.
///     3. client tells buffer manager to `unpin` the page when done.
///     When a page in `unpinned`, BM is allowed to load a different disk block to it.
///
/// There are 4 scenarios when client asks BM to pin a page to a disk block:
/// 1. block content `already in` a buffer pool page. page `pinned` by other client.
/// 2. block content `already in` a buffer pool page. page `unpinned`.
/// 3. block content `not in` in a buffer pool. there is >=1 `unpinned` page to store block.
/// 4. block content `not in` a buffer pool. all pages in buffer are `pinned`.
///
/// Scenario 1:
/// - one or more clients are currently accessing the contents of the block.
/// - BM pins the page again and returns it as a page can be `pinned` multiple times.
/// - each client can read and modify page concurrently.
/// - conflicts are `transaction manager` concern, not BM.
///
/// Scenario 2:
/// - BM pins the page and returns to client.
///
/// Scenario 3:
/// - BM select which `unpinned` page to reuse with `buffer replacement strategies` like LRU.
/// - flushing that unpinned page to disk if it was modified.
/// - read the disk block into this page.
/// - pin the page
/// - return to client
///
/// Scenario 4:
/// - place client on `wait list`
/// - do scenario 3 when an unpinned page is available.
///
pub struct BufferManager {

}