# Change Log

## [1.0.2] - Unreleased

### Added
- add new java8 sql types:

| Field Type|JDBC Type|java type|
|-----------|---------|---------|
|TIMEZ       | TIME_WITH_TIMEZONE (NOT NULL)| OffsetTime |
|TIMEZ?      | TIME_WITH_TIMEZONE (NULL)| OffsetTime |
|TIMESTAMPZ  | TIMESTAMP_WITH_TIMEZONE (NOT NULL)| OffsetDateTime |
|TIMESTAMPZ? | TIMESTAMP_WITH_TIMEZONE (NULL)| OffsetDateTime |

## [1.0.1] - 2025-04-06

### Fixed
- changed regex for select (BUG in subselect). <br>
From `(INTO (.*)) FROM` to `(INTO\s+(:\w+[.\w+]*(\s*,\s*:\w+[.\w+]*)*))\s+FROM`


## [1.0.0] - 2025-02-14 - baseline version
