package aug.common.gateway.helper;

import aug.common.gateway.consts.ServiceCode;
import aug.common.gateway.db.dao.RateLimitDAO;
import aug.common.gateway.db.dao.ServiceDAO;
import aug.common.gateway.db.po.RateLimit;
import aug.common.gateway.db.po.ServiceInfo;
import com.google.common.collect.ArrayListMultimap;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author guoxiaoyong
 * @date 2020/8/14
 */
public class PropertiesHelper {

    private final Random random = new Random();

    private final AtomicReference<ArrayListMultimap<ServiceCode, ServiceInfo>> servicesRef = new AtomicReference<>();
    private final AtomicReference<ArrayListMultimap<ServiceCode, RateLimit>> rateLimitsRef = new AtomicReference<>();

    private final ServiceDAO serviceDAO;
    private final RateLimitDAO rateLimitDAO;

    public PropertiesHelper(ServiceDAO serviceDAO, RateLimitDAO rateLimitDAO) {
        this.serviceDAO = serviceDAO;
        this.rateLimitDAO = rateLimitDAO;
    }

    public ServiceInfo getServiceInfo(ServiceCode serviceCode) {
        List<ServiceInfo> services = servicesRef.get().get(serviceCode);
        List<ServiceInfo> onlineServices = services.parallelStream()
                .filter(serviceConfig -> ServiceInfo.ServiceStatus.ONLINE.equals(serviceConfig.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(onlineServices)) {
            // 该服务集群全部离线
            return null;
        }
        return onlineServices.get(random.nextInt(onlineServices.size()));

    }

    public List<RateLimit> getRateLimits(ServiceCode serviceCode) {
        List<RateLimit> rateLimits = rateLimitsRef.get().get(serviceCode);
        // 按级别排序
        rateLimits.sort(Comparator.comparingInt(r -> r.getLimitType().level));
        return rateLimits;
    }

    public void start() {
        this.reloadServices();
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::reloadServices, 10, 10, TimeUnit.SECONDS);
    }

    private void reloadServices() {
        List<ServiceInfo> services = serviceDAO.selectAll();
        ArrayListMultimap<ServiceCode, ServiceInfo> serviceSum = ArrayListMultimap.create();
        services.forEach(serviceConfig -> {
            if (ServiceInfo.ServiceStatus.OFFLINE.equals(serviceConfig.getStatus())) {
                // 服务不在线
            } else {
                serviceSum.put(serviceConfig.getServiceCode(), serviceConfig);
            }
        });
        servicesRef.set(serviceSum);

        List<RateLimit> limits = rateLimitDAO.selectAll();
        ArrayListMultimap<ServiceCode, RateLimit> limitConfigMap = ArrayListMultimap.create();
        limits.forEach(rateLimit -> limitConfigMap.put(rateLimit.getServiceCode(), rateLimit));


        rateLimitsRef.set(limitConfigMap);
    }

}
