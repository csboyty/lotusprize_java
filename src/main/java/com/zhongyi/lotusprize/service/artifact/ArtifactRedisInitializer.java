package com.zhongyi.lotusprize.service.artifact;

import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.zhongyi.lotusprize.domain.artifact.Artifact;
import com.zhongyi.lotusprize.mapper.artifact.ArtifactMapper;
import com.zhongyi.lotusprize.redis.AccountRedis;
import com.zhongyi.lotusprize.redis.ArtifactRedis;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;

public class ArtifactRedisInitializer extends BaseArtifactHandler {

    @Autowired
    private ArtifactMapper artifactMapper;

    @Autowired
    private ArtifactRedis artifactRedis;

    @PostConstruct
    public void init() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("artifact redis start ");
                ArtifactIterator artifactIt = new ArtifactIterator(0, 20);
                while(artifactIt.hasNext()){
                    final Artifact artifact = artifactIt.next();
                    artifactRedis.execute(new RedisOperation(){
                        @Override
                        public Void doWithRedis(Jedis jedis) {
                            Transaction tx = jedis.multi();
                            tx.hmset(ArtifactRedis.artifactKey(artifact.getId()), ArtifactRedis.toStringMap(artifact));
                            tx.sadd(AccountRedis.accountArtifactKey(artifact.getOwnAccountId()), String.valueOf(artifact.getId()));
                            tx.sadd(AccountRedis.accountArtifactKey(topicOwnAccountId(artifact.getTopicId())),  String.valueOf(artifact.getId()));
                            tx.exec();
                            return null;
                        }});
                }
                System.out.println("artifact redis finished");

            }

        };
        new Thread(r).start();
    }

    private class ArtifactIterator implements Iterator<Artifact> {

        private final int limit;

        private int offsetArtifactId;

        private Iterator<Artifact> _iterator;

        private ArtifactIterator(int offsetArtifactId, int limit) {
            this.offsetArtifactId = offsetArtifactId;
            this.limit = limit;
            _iterator = artifactMapper.iterArtifact(offsetArtifactId, limit)
                    .iterator();
        }

        @Override
        public boolean hasNext() {
            boolean _hasNext = _iterator.hasNext();
            if (!_hasNext) {
                _iterator = artifactMapper
                        .iterArtifact(offsetArtifactId, limit).iterator();
                _hasNext = _iterator.hasNext();
            }
            return _hasNext;
        }

        @Override
        public Artifact next() {
            Artifact artifact = _iterator.next();
            offsetArtifactId = artifact.getId();
            return artifact;
        }

        @Override
        public void remove() {

        }
    }
}
