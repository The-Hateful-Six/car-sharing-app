package thehatefulsix.carsharingapp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import thehatefulsix.carsharingapp.dto.ads.AdvertDto;
import thehatefulsix.carsharingapp.dto.ads.CreateAdvertRequestDto;

public interface AdvertService {
    AdvertDto save(CreateAdvertRequestDto requestDto);

    List<AdvertDto> findAll(Pageable pageable);

    void deleteById(Long id);

    AdvertDto getAddById(Long id);

    AdvertDto update(CreateAdvertRequestDto requestDto, Long id);

    void sentAdvert();
}
