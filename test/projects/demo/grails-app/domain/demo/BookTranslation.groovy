package demo

import com.ticketbis.groobalize.Translation
import com.ticketbis.groobalize.ast.Field

class BookTranslation extends Translation<Book> {
    @Field(inherit=false)
    String title

    String synopsis = null

    static constraints = {
        synopsis(nullable: true)
    }
}
