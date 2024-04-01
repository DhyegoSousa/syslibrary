package br.com.lbnetwork.syslibrary.controllers.book;

import br.com.lbnetwork.syslibrary.dtos.book.BookRecordDto;
import br.com.lbnetwork.syslibrary.models.book.*;
import br.com.lbnetwork.syslibrary.repositories.book.*;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/")
public class BookController {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    PublisherRepository publisherRepository;

    @PostMapping("/books")
    public ResponseEntity<?> createBook(@RequestBody @Valid BookRecordDto bookRecordDto){
        var bookModelOptional = bookRepository.findByTitle(bookRecordDto.title());
        if (bookModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(bookModelOptional.get());
        }
        var bookModel = new BookModel();
        BeanUtils.copyProperties(bookRecordDto, bookModel);

        if (bookRecordDto.author() != null && bookRecordDto.author().getIdAuthor() != null){
            UUID authorId = bookRecordDto.author().getIdAuthor();
            Optional<AuthorModel> authorModelOptional = authorRepository.findById(authorId);
            if (authorModelOptional.isPresent()){
                bookModel.setAuthor(authorModelOptional.get());
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found with the given ID: " +authorId);
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Author ID not provided in DTO.");
        }

        if (bookRecordDto.category() != null && bookRecordDto.category().getIdCategory() != null){
            UUID categoryId = bookRecordDto.category().getIdCategory();
            Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);
            if (categoryModelOptional.isPresent()){
                bookModel.setCategory(categoryModelOptional.get());
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with the given ID: " +categoryId);
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category ID not provided in DTO.");
        }

        if (bookRecordDto.language() != null && bookRecordDto.language().getIdLanguage() != null){
            UUID languageId = bookRecordDto.language().getIdLanguage();
            Optional<LanguageModel> languageModelOptional = languageRepository.findById(languageId);
            if (languageModelOptional.isPresent()){
                bookModel.setLanguage(languageModelOptional.get());
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Language not found with the given ID: " +languageId);
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Language ID not provided in DTO.");
        }

        if (bookRecordDto.publisher() != null && bookRecordDto.publisher().getIdPublisher() != null){
            UUID publisherId = bookRecordDto.publisher().getIdPublisher();
            Optional<PublisherModel> publisherModelOptional = publisherRepository.findById(publisherId);
            if (publisherModelOptional.isPresent()){
                bookModel.setPublisher(publisherModelOptional.get());
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publisher not found with the given ID: " +publisherId);
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Publisher ID not provided in DTO.");
        }
        bookModel.setTitle(bookRecordDto.title().toUpperCase());
        bookModel.setCreatedAt(new Date());
        bookModel.setUpdatedAt(new Date());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookRepository.save(bookModel));
    }


}
