package Source.Services;

import Source.Models.WatchHistory;
import Source.Repositories.WatchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class WatchHistoryService {
    private final WatchHistoryRepository whRepository;

    @Autowired
    public WatchHistoryService(WatchHistoryRepository whRepository){
        this.whRepository = whRepository;
    }

    public WatchHistory save(WatchHistory watchHistory){
        return whRepository.save(watchHistory);
    }

    public List<WatchHistory> findAll(){
        return whRepository.findAll();
    }

    public WatchHistory findById(int id){
        return whRepository.findById(id).orElse(null);
    }

    public void update(WatchHistory watchHistory){
        WatchHistory wh = whRepository.findById(watchHistory.getWatchId())
                .orElse(null);
        if(wh != null){
            wh.setTimeWatch(watchHistory.getTimeWatch());

            wh.setUser(watchHistory.getUser());
            wh.setMovie(watchHistory.getMovie());

            whRepository.save(wh);
        }
    }

    public void delete(WatchHistory watchHistory){
        whRepository.delete(watchHistory);
    }

    public Page<WatchHistory> findByUserId(int userId, Pageable pageable){
        List<WatchHistory> watchHistories = whRepository.findAll()
                .stream()
                .filter(watchHistory -> watchHistory.getUser().getUserId() == userId)
                .sorted(Comparator.comparingInt(WatchHistory::getWatchId).reversed())
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), watchHistories.size());
        return new PageImpl<>(watchHistories.subList(start, end), pageable, watchHistories.size());
    }
}
