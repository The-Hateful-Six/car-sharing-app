package thehatefulsix.carsharingapp.service.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.ads.AdvertDto;
import thehatefulsix.carsharingapp.dto.ads.CreateAdvertRequestDto;
import thehatefulsix.carsharingapp.exception.EntityNotFoundException;
import thehatefulsix.carsharingapp.mapper.AdvertMapper;
import thehatefulsix.carsharingapp.model.Advert;
import thehatefulsix.carsharingapp.repository.AdvertRepository;
import thehatefulsix.carsharingapp.service.AdvertService;
import thehatefulsix.carsharingapp.service.TelegramBotService;

@Service
@RequiredArgsConstructor
public class AdvertServiceImpl implements AdvertService {
    private static final String CHANNEL_USERNAME = "@brum_brum_car_sharing_6";
    private final AdvertMapper advertMapper;
    private final AdvertRepository advertRepository;
    private final TelegramBotService telegramBotService;

    @Override
    public AdvertDto save(CreateAdvertRequestDto requestDto) {
        Advert advert = advertMapper.toAds(requestDto);
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @Override
    public List<AdvertDto> findAll(Pageable pageable) {
        return advertRepository.findAll(pageable).stream()
                .map(advertMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        advertRepository.deleteById(id);
    }

    @Override
    public AdvertDto getAddById(Long id) {
        return advertMapper.toDto(advertRepository.getAdsById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Can't find "
                                + "the advert by this id: " + id)));
    }

    @Override
    public AdvertDto update(CreateAdvertRequestDto requestDto, Long id) {
        Advert advert = advertRepository.getAdsById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find the advert by id " + id)
        );
        advertMapper.updateAdvert(requestDto, advert);
        advert.setId(id);
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @Transactional
    public void sentAdvert() {
        LocalDateTime today = LocalDateTime.now();
        List<Advert> adverts = advertRepository.findAll().stream()
                .filter(a -> a.getSendTime().getDayOfYear() == today.getDayOfYear())
                .filter(a -> a.getSendTime().getHour() == today.getHour())
                .filter(a -> a.getSendTime().getMinute() == today.getMinute())
                .toList();
        for (Advert advert : adverts) {
            if (advert.getPhotoUrl() != null) {
                telegramBotService.sendMessageWithPhotoToGroup(CHANNEL_USERNAME,
                        advert.getText(), advert.getPhotoUrl());
            } else {
                telegramBotService.sendMessageToCertainGroup(CHANNEL_USERNAME, advert.getText());
            }
        }
    }
}
