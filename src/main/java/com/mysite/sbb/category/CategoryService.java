package com.mysite.sbb.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public List<Category> getCategoryList(){
        return this.categoryRepository.findAll();
    }

    public Category getCategory(String category) {
        return this.categoryRepository.findByName(category);
    }
}
