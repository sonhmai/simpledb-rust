use crate::filesystem::FileManager;

/// Responsible for reading from and writing log record to log file.
/// LogManager supports only sequential access to the log file.
/// It uses TODO as memory-management algo.
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
pub struct LogManager {}

impl LogManager {
    pub fn new(file_manager: FileManager, logfile: String) -> LogManager {
        LogManager {}
    }

    pub fn append(&self, record: &[u8]) -> usize {
        todo!()
    }

    pub fn flush(&self, lsn: usize) -> () {
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