use crate::filesystem::FileManager;
use crate::memory::lsn::LSN;

/// Responsible for reading from and writing log record to log file.
/// LogManager supports only sequential access to the log file.
///
/// `LogManager` does not understand the contents of `log records`, treat them as byte array `&[u8]`.
/// Understanding the serde of the bytes and log record is the job of `RecoveryManager`.
///
/// Whenever a user changes the database, the database engine must keep track of that change in case it needs to be undone.
/// - The values describing a change are kept in a log record, and the log records are stored in a log file.
/// - New log records are appended to the end of the log.
///
/// A simple but inefficient algo for appending a new record to the log.
/// 1. Allocate a page in mem.
/// 2. Read last block of log file into that page (block size == page size).
/// 3. Check if page has enough space for new log record
///    - If there is room, place the log record after the other records on the page,
/// and write the page back to disk.
///    - If there is no room, then allocate a new, empty page, place the log record in that page,
/// and append the page to a new block at the end of the log file.
///
/// The optimal log management algo:
/// 1. Permanently allocate one memory page to hold the contents of the last block of the log file.
/// Call this page `P`.
/// 2. When a new log record is submitted
///     - if there is no room in P => write P to disk and clear its content.
///     - Append the new log record to P.
/// 3. When db requests that a particular log record be written to disk (e.g. by a transaction commit)
///     - determine if that log record is in P.
///     - if so, write P to disk.
///
/// The simple algo needs a disk read and write for every log record.
/// This is slower than the optimal algo.
///
/// The optimal algo has 2 places where disk write is needed -> `less than the simple algo`
/// 1. where the page is full
/// 2. where a log record is needed to be persisted
///
/// There is one issue (will be addressed in `transaction management` later):
/// - `buffer manager` must wait for all a page `related log records` written to disk by `log manager`
/// - before it can write a `modified data page` to disk
/// - to ensure redo can be done if necessary later
#[derive(Debug)]
pub struct LogManager {}

impl LogManager {
    pub fn new(file_manager: FileManager, logfile: String) -> LogManager {
        LogManager {}
    }

    /// add record to the log and return its `Log Sequence Number` (LSN).
    /// The only constraint `LogManager` cares about is the array must fit inside a page.
    /// It treats the record simply as a byte array.
    ///
    /// `Append` does not guarantee the record is written to disk. A client can force a specific
    /// LSN to disk by calling `flush`.
    pub fn append(&self, record: &[u8]) -> LSN {
        todo!()
    }

    /// Ensures that this lsn `log record` and all before it are written to disk.
    pub fn flush(&self, lsn: LSN) -> () {
        todo!()
    }

    /// returns an iterator of all the records in the log.
    ///
    /// Records returned are in reverse order, starting at the latest record and moving backwards.
    /// This is the expected order by `RecoveryManager` because we must undo the latest
    /// transaction first, then the later ones.
    pub fn iterator(&self) -> Box<dyn Iterator<Item=Vec<u8>>> {
        todo!()
    }
}


#[cfg(test)]
mod tests {}