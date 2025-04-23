# Change Log

## [1.0.3] - Unreleased

### Added
- optional custom query validator via SPI

### Fixed
- missed import for type DECIMAL?


## [1.0.2] - 2025-04-21

### Added
- added new java8 sql types:

| Field Type|JDBC Type|java type|
|-----------|---------|---------|
|TIMEZ       | TIME_WITH_TIMEZONE (NOT NULL)| OffsetTime |
|TIMEZ?      | TIME_WITH_TIMEZONE (NULL)| OffsetTime |
|TIMESTAMPZ  | TIMESTAMP_WITH_TIMEZONE (NOT NULL)| OffsetDateTime |
|TIMESTAMPZ? | TIMESTAMP_WITH_TIMEZONE (NULL)| OffsetDateTime |

- added `qualifier` configuration field to root level, to select datasource (SPRING provider)

### Changed
- `@Autowired` from field to constructor (SPRING provider)

## [1.0.1] - 2025-04-06

### Fixed
- changed regex for select (BUG in subselect). <br>
From `(INTO (.*)) FROM` to `(INTO\s+(:\w+[.\w+]*(\s*,\s*:\w+[.\w+]*)*))\s+FROM`


## [1.0.0] - 2025-02-14 - baseline version
