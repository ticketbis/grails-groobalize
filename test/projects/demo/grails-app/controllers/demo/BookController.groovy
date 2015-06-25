package demo

class BookController {

    def index() {
        def book = new Book()
        def translations = book.translations
        render(text: translations)
    }
}
