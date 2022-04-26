package com.jeremiahpierce.imageanalyze.common;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

/**
 * This helper class helps with mapping
 */
public class MapperUtil {

    private static ModelMapper modelMapper = new ModelMapper();

    private MapperUtil() {
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> clazz) {

        return source
                .stream()
                .map(element -> modelMapper.map(element, clazz))
                .collect(Collectors.toList());
    }
}
