package com.zhongyi.lotusprize.service.artifact;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.zhongyi.lotusprize.domain.artifact.Artifact;
import com.zhongyi.lotusprize.domain.artifact.ArtifactIntroduce;
import com.zhongyi.lotusprize.mapper.artifact.ArtifactResultMapper;
import com.zhongyi.lotusprize.redis.AccountRedis;
import com.zhongyi.lotusprize.redis.ArtifactRedis;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;
import com.zhongyi.lotusprize.service.AppListeningExecutorService;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.service.LotusprizeLocalFiles;
import com.zhongyi.lotusprize.service.SystemEventBus;
import com.zhongyi.lotusprize.service.bcs.LotusprizeBcsFiles;
import com.zhongyi.lotusprize.util.JsonUtil;

@Component
public class ArtifactUserOperationHandler extends BaseArtifactHandler {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	
	@Autowired
	private ArtifactResultMapper artifactResultMapper;
	
	

	private final Function<ArtifactIntroduce,String> artifactIntroduceImageFunction = new Function<ArtifactIntroduce,String>(){
		@Override
		public String apply(ArtifactIntroduce input) {
			return input.getImage();
		}
		
	};
	

	public void save(Artifact artifact) throws IOException {
		ArtifactSaveOperation operation = new ArtifactSaveOperation(artifact);
		operation.doSave();
		operation.postSave();
	}
	
	public void remove(final Integer accountId,final Integer artifactId){
		final Integer topicId = artifactRedis.artifactTopicId(artifactId);
		
		txRunner.doInTransaction(new ITransactionOperation(){

			@Override
			public Void run() {
				artifactMapper.deleteByArtifactId(artifactId);
				artifactMapper.deleteArtifactResultByArtifactId(artifactId);
				artifactMapper.deleteArtifactIntroduceByArtifactId(artifactId);
				topicMapper.incrArtifactAmount(topicId, -1);
				artifactRedis.execute(new RedisOperation(){

					@Override
					public Void doWithRedis(Jedis jedis) {
						Transaction tx = jedis.multi();
						tx.del(ArtifactRedis.artifactKey(artifactId));
						tx.hincrBy(TopicRedis.topicKey(topicId), "artifactAmount", -1);
						tx.srem(AccountRedis.accountArtifactKey(accountId), String.valueOf(artifactId));
						tx.srem(AccountRedis.accountArtifactKey(topicOwnAccountId(topicId)), String.valueOf(artifactId));
						tx.exec();
						return null;
					}
					
				});
				return null;
			}
		});
		LotusprizeLocalFiles.instance().deletePdfByArtifactId(artifactId);
		
	}

	private class ArtifactSaveOperation {
		
		private final Artifact artifact;
		private final List<Runnable> postRunnables = Lists.newArrayList();

		private ArtifactSaveOperation(Artifact artifact) {
			this.artifact = artifact;
		}
		
		private void doSave() throws IOException{
			final Artifact oldArtifact = artifact.getId()!= null ? artifactRedis.getArtifact(artifact.getId()): null;
			String oldArtifactProfile = null;
			String oldArtifactAttachment = null;
			Collection<String> oldIntroduceImages =  Collections.emptyList();
			if(oldArtifact != null){
			    artifact.setStatus(oldArtifact.getStatus());
				oldArtifactProfile = oldArtifact.getProfile();
				oldArtifactAttachment = oldArtifact.getAttachment();
				oldIntroduceImages = Collections2.transform(oldArtifact.getIntroduces(), artifactIntroduceImageFunction);
			}
			String newArtifactProfile = toLocalHttpFile(oldArtifactProfile,artifact.getProfile(),artifact.getOwnAccountId());
			artifact.setProfile(newArtifactProfile);
			String newArtifactAttachment = toLocalHttpFile(oldArtifactAttachment,artifact.getAttachment(),artifact.getOwnAccountId());
			artifact.setAttachment(newArtifactAttachment);
			
			if(artifact.getIntroduces()!= null && !artifact.getIntroduces().isEmpty()){
				for(ArtifactIntroduce introduce:artifact.getIntroduces()){
					String image = toLocalHttpFile(null,introduce.getImage(),artifact.getOwnAccountId());
					introduce.setImage(image);
				}
			}
			
			txRunner.doInTransaction(new ITransactionOperation(){
				@Override
				public Object run() {
					
					if(oldArtifact != null){
						artifactMapper.deleteArtifactIntroduceByArtifactId(artifact.getId());
						artifactMapper.updateArtifact(artifact);
					}else{
						artifactMapper.insertArtifact(artifact);
						artifactResultMapper.insertArtifactResult(artifact.getId());
						topicMapper.incrArtifactAmount(artifact.getTopicId(), 1);
						
					}
					if(artifact.getIntroduces()!= null && !artifact.getIntroduces().isEmpty())
						artifactMapper.insertArtifactIntroduces(artifact.getId(), artifact.getIntroduces());
					
					artifactRedis.execute(new RedisOperation(){
						@Override
						public Object doWithRedis(Jedis jedis) {
							Transaction tx = jedis.multi();
							if(oldArtifact ==null){
								tx.hincrBy(TopicRedis.topicKey(artifact.getTopicId()), "artifactAmount", 1);
							}
							tx.hmset(ArtifactRedis.artifactKey(artifact.getId()), ArtifactRedis.toStringMap(artifact));
							tx.sadd(AccountRedis.accountArtifactKey(artifact.getOwnAccountId()), String.valueOf(artifact.getId()));
							tx.sadd(AccountRedis.accountArtifactKey(topicOwnAccountId(artifact.getTopicId())),  String.valueOf(artifact.getId()));
							tx.exec();
							return null;
						}
						
					});
					return null;
				}
				
			});
			if(oldArtifact != null){
                LotusprizeLocalFiles.instance().deletePdfByArtifactId(artifact.getId());
            }
			
			postRunnables.add(new ArtifactIntroduceBcsAddRunnable(artifact.getId(),artifact.getIntroduces(),oldIntroduceImages));
			postRunnables.add(new ArtifactBcsAddFileRunnable(oldArtifactProfile,newArtifactProfile,new UpdateProfileFutureCallback(artifact.getId(),newArtifactProfile)));
			
		}
		
		private void postSave(){
			for(Runnable runnable:postRunnables){
				runnable.run();
			}
		}

	}

	
	private class ArtifactIntroduceBcsAddRunnable implements Runnable{
		
		private final Integer artifactId;
		private final Collection<ArtifactIntroduce> artifactIntroduces;
		private final Collection<String> oldIntroduceImages;
		
		public ArtifactIntroduceBcsAddRunnable(
				Integer artifactId,
				Collection<ArtifactIntroduce> artifactIntroduces,
				Collection<String> oldIntroduceIamges) {
			super();
			this.artifactId = artifactId;
			this.artifactIntroduces = artifactIntroduces;
			this.oldIntroduceImages = oldIntroduceIamges;
		}
		
		public void run(){
			final List<ListenableFuture<ArtifactIntroduce>> futures = Lists.newArrayListWithCapacity(artifactIntroduces.size());
			for(final ArtifactIntroduce introduce:artifactIntroduces){
				if(oldIntroduceImages.contains(introduce.getImage())){
					oldIntroduceImages.remove(introduce.getImage());
					final ListenableFuture<ArtifactIntroduce> future = MoreExecutors.sameThreadExecutor().submit(new Callable<ArtifactIntroduce>(){
						@Override
						public ArtifactIntroduce call() throws Exception {
							return introduce;
						}
					});
					futures.add(future);
				}else if(LotusprizeLocalFiles.instance().isLocalFile(introduce.getImage())){
					final ListenableFuture<ArtifactIntroduce> future = AppListeningExecutorService.instance().executor().submit(
						new Callable<ArtifactIntroduce>(){
							public ArtifactIntroduce call() throws Exception {
								SystemEventBus.BcsAddFileEvent bcsAddFileEvent = new SystemEventBus.BcsAddFileEvent(introduce.getImage());
								SystemEventBus.instance().post(bcsAddFileEvent);
								if(bcsAddFileEvent.getEx() != null)
									throw bcsAddFileEvent.getEx();
								introduce.setImage(bcsAddFileEvent.getResult());
								return introduce;
							}
					});
					futures.add(future);
				}
			}
			ListenableFuture<List<ArtifactIntroduce>> introducesFuture = Futures.allAsList(futures);
			Futures.addCallback(introducesFuture, new FutureCallback<List<ArtifactIntroduce>>(){
				@Override
				public void onSuccess(final List<ArtifactIntroduce> result) {
					txRunner.doInTransaction(new ITransactionOperation(){
						@Override
						public Void run() {
							for(ArtifactIntroduce introduce:result){
								artifactMapper.updateIntroduceImage(artifactId, introduce.getPos(), introduce.getImage());
							}
							artifactRedis.execute(new RedisOperation(){
								@Override
								public Void doWithRedis(Jedis jedis) {
									String artifactKey = ArtifactRedis.artifactKey(artifactId);
									jedis.hset(artifactKey,"introduces",JsonUtil.toJsonString(artifactIntroduces));
									return null;
								}
							});
							return null;
						}
					});
					for(String oldImage:oldIntroduceImages){
						if(LotusprizeLocalFiles.instance().isLocalFile(oldImage)){
							File file = LotusprizeLocalFiles.instance().toLocalFile(oldImage);
							try {
								Files.deleteIfExists(file.toPath());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else if(LotusprizeBcsFiles.instance().isBcsFile(oldImage)){
							SystemEventBus.BcsRemoveFileEvent bcsRemoveFileEvent =new SystemEventBus.BcsRemoveFileEvent(oldImage);
							SystemEventBus.instance().post(bcsRemoveFileEvent);
						}
					}
					
				}

				@Override
				public void onFailure(Throwable t) {
					
				}
				
			});
		}
		
		
	}
	
	
	
	private class ArtifactBcsAddFileRunnable implements Runnable{
		private final String oldFileUri;
		private final String newFileUri;
		private final FutureCallback<String> futureCallback;

		public ArtifactBcsAddFileRunnable(String oldFileUri,
				String newFileUri,	FutureCallback<String> futureCallback) {
			super();
			this.oldFileUri = oldFileUri;
			this.newFileUri = newFileUri;
			this.futureCallback = futureCallback;
		}


		@Override
		public void run() {
			if (LotusprizeLocalFiles.instance().isLocalFile(newFileUri)
					&& !newFileUri.equals(oldFileUri)){
				ListenableFuture<String> future = AppListeningExecutorService.instance().executor().submit(new Callable<String>(){
					@Override
					public String call() throws Exception {
						SystemEventBus.BcsAddFileEvent bcsAddFileEvent = new SystemEventBus.BcsAddFileEvent(newFileUri);
						SystemEventBus.instance().post(bcsAddFileEvent);
						if(bcsAddFileEvent.getEx() != null)
							throw bcsAddFileEvent.getEx();
						return bcsAddFileEvent.getResult();
					}
				});
				Futures.addCallback(future,futureCallback);
				Futures.addCallback(future, new FutureCallback<String>(){
					@Override
					public void onSuccess(String result) {
						try{
							if(LotusprizeLocalFiles.instance().isLocalFile(oldFileUri)){
								File file = LotusprizeLocalFiles.instance().toLocalFile(oldFileUri);
								Files.deleteIfExists(file.toPath());
							}else if(LotusprizeBcsFiles.instance().isBcsFile(oldFileUri)){
								SystemEventBus.BcsRemoveFileEvent bcsRemoveFileEvent =new SystemEventBus.BcsRemoveFileEvent(oldFileUri);
								SystemEventBus.instance().post(bcsRemoveFileEvent);
							}
//							if(LotusprizeLocalFiles.instance().isLocalFile(newFileUri))
//								Files.deleteIfExists(LotusprizeLocalFiles.instance().toLocalFile(newFileUri).toPath());
						}catch(Exception ex){
							logger.error("",ex);
						}
						
					}

					@Override
					public void onFailure(Throwable t) {
						
					}
					
				});
			}
			
		}
	}
	
	private class UpdateProfileFutureCallback implements FutureCallback<String> {

		private final Integer artifactId;
		
		private final String profile;

		public UpdateProfileFutureCallback(Integer artifactId,String profile) {
			super();
			this.artifactId = artifactId;
			this.profile = profile;
		}

		@Override
		public void onSuccess(final String corpLogo) {
			txRunner.doInTransaction(new ITransactionOperation() {
				@Override
				public Void run() {
					artifactMapper.updateArtifactProfile(artifactId, profile);
					artifactRedis.execute(new RedisOperation(){
						@Override
						public Void doWithRedis(Jedis jedis) {
							String artifactKey = ArtifactRedis.artifactKey(artifactId);
							jedis.hset(artifactKey,"profile",profile);
							return null;
						}
					});
					return null;
				}
			});

		}

		@Override
		public void onFailure(Throwable t) {
			logger.error("上传作品縮略至百度云存储错误,artifactId:" + artifactId + ",profile:"
					+ profile, t);
		}

	}
	
}
