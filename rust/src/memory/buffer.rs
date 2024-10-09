use std::sync::Arc;
use crate::filesystem::{FileManager, Page};
use crate::memory::LogManager;

/// Buffer contains status info of page in buffer pool
/// - where it's pinned: what block it's assigned to
/// - how many times its page is pinned
/// - whether a page has been modified, by which transaction
/// - `log information`: if page modified, buffer has LSN of most recent log record.
///
/// Buffer
/// - observes changes to its page
/// - responsible for writing modified page to disk
/// - can reduce disk accesses by delay writing and write in batch, `like a log`.
/// - a reasonable strategy is to delay writing until page is `unpinned`.
///
/// The only 2 reasons why a buffer needs to write a modified page to disk
/// 1. page is being replaced because buffer `pinned` to another block.
/// 2. `recovery manager` needs to write its content to disk to guard against system crash.
#[derive(Debug)]
pub struct Buffer {
    fm: Arc<FileManager>,
    lm: Arc<LogManager>,
    /// how many times its page was pinned
    pins: usize,
}

impl Buffer {
    pub fn new(fm: Arc<FileManager>, lm: Arc<LogManager>) -> Self {
        Self {
            fm: fm.clone(),
            lm: lm.clone(),
            pins: 0,
        }
    }

    /// gets the buffered page.
    ///
    /// Client is responsible for
    /// 1. creating log record
    /// 2. calling buffer `setModified` method
    pub fn contents(&self) -> Page {
        todo!()
    }

    /// persists the page in memory to disk block.
    pub fn flush(&mut self) {
        // if page not modified -> do nothing
        // if page modified -> ensure log record is on disk first, then write the page
        todo!()
    }

    pub fn pin(&mut self) -> () {
        self.pins += 1;
    }

    pub fn is_pinned(&self) -> bool {
        self.pins > 0
    }

    pub fn unpin(&mut self) -> () {
        self.pins -= 1;
    }
}
