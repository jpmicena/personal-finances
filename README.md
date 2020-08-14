# Personal Finances

WIP, a personal finances system backed by a ledger-like data model

It's 100% tailored to my own use case, and also a project to learn more about Clojure

## General Idea
Have a cmd line interface to control my own personal finances.
The idea is to use a simplified double entry model,
replacing the `credit/debit` concepts for a simpler `increasing/decreasing` view.
Although it's not 100% following account principles, it is heavily inspired by it.
Also, a lot of ideas on this project were inspired by [plain text accounting](https://plaintextaccounting.org/)

All entries will happen in two accounts at once as if the money is flowing from one to another,
i.e., one account will `decrease` its balance,
while the other account will `increase` its balance

The main account categories are: `income`, `expense`, `asset`, `liability`.
If you sum the balances of all accounts, it should always be zero.


## TODO list
- Make cmd handler a component
- Be able to have test/production databases as a component option
- Be able to add help for the commands
- General Functionality
  - ~~Basic component system layed out~~
  - ~~Interface for adding commands (using clj spec)~~
  - ~~Account interaction (add, remove, list)~~
  - Entry interaction (add, remove, list)
  - Credit card bills (attach entries, control payments, etc.)
  - Routines (fixed payments, execute future entries, etc.)
  - txt inputs (for instance, executing multiple commands)
  - Add constraints on foreign keys (e. g., block account deletion if there's an entry associated)
  - Use `spec coerce` lib to coerce cmd arguments
  - Create helpers to format command returns
