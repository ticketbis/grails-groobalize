package demo

import com.ticketbis.groobalize.Translation
import com.ticketbis.groobalize.ast.Field

class BookTranslation extends Translation<Book> {
    String title

    @Field(inherit=false)
    String synopsis
}
