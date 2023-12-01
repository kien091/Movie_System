package Source.Services;

import Source.Models.WatchHistory;
import Source.Repositories.WatchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            wh.setTimeStamp(watchHistory.getTimeStamp());
            wh.setTimeWatch(watchHistory.getTimeWatch());

            wh.setUser(watchHistory.getUser());
            wh.setMovie(watchHistory.getMovie());

            whRepository.save(wh);
        }
    }

    public void delete(WatchHistory watchHistory){
        whRepository.delete(watchHistory);
    }
}
