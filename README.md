## 📡 API Endpoints

### 📚 Books
| Method | Endpoint | Description |
|--------|---------|------------|
| POST | /api/books | Add a new book |
| GET | /api/books | Get all books |
| GET | /api/books/available | Get available books |
| GET | /api/books/inventory | Get stock summary |
| GET | /api/books/search?q= | Search books |
| PUT | /api/books/{id} | Update book |
| DELETE | /api/books/{id} | Delete book |

### 👤 Members
| Method | Endpoint | Description |
|--------|---------|------------|
| POST | /api/members | Register member |
| GET | /api/members | Get all members |
| GET | /api/members/{id}/fines | Get total fines |
| PATCH | /api/members/{id}/status | Update status |

### 🔄 Borrow System
| Method | Endpoint | Description |
|--------|---------|------------|
| POST | /api/borrow | Borrow book |
| PUT | /api/borrow/{id}/return | Return book |
| PUT | /api/borrow/{id}/pay-fine | Pay fine |
| GET | /api/borrow/overdue | Get overdue books |
| GET | /api/borrow/dashboard | Dashboard stats |
| POST | /api/borrow/update-overdue | Refresh overdue & fines |
