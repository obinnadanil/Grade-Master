package obi.com.grademaster.mapper;

import obi.com.grademaster.DTO.CourseDto;
import obi.com.grademaster.entity.Course;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.FIELD)
public  abstract class CourseMapper {


    @Mapping(target = "scores", ignore = true)
    public abstract Course mapToCourse(CourseDto courseDto);

    public abstract CourseDto mapToCourseDto(Course course);



}
