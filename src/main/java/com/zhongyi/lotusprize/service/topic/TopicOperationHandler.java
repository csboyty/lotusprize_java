package com.zhongyi.lotusprize.service.topic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.zhongyi.lotusprize.domain.topic.Topic;
import com.zhongyi.lotusprize.domain.topic.TopicDetail;
import com.zhongyi.lotusprize.domain.topic.TopicIntroduce;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;
import com.zhongyi.lotusprize.service.AppListeningExecutorService;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.service.LotusprizeLocalFiles;
import com.zhongyi.lotusprize.service.SystemEventBus;
import com.zhongyi.lotusprize.service.bcs.LotusprizeBcsFiles;
import com.zhongyi.lotusprize.util.JsonUtil;

@Component
public class TopicOperationHandler extends BaseTopicHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final DeleteBcsFileHandler deleteBcsFileHandler = new DeleteBcsFileHandler();


//	@Autowired
//	private VelocityEngine appVelocityEngine;
	
	private final TypeReference<TopicDetail> topicDetailType = new TypeReference<TopicDetail>(){};
	
	private final Function<TopicIntroduce,String> topicIntroduceImageFunction = new Function<TopicIntroduce,String>(){
		@Override
		public String apply(TopicIntroduce input) {
			return input.getImage();
		}
		
	};

	public void createOrUpdateTopic(Topic topic, TopicDetail topicDetail) throws IOException {
		CreateOrUpdateOperation operation = new CreateOrUpdateOperation(topic,topicDetail);
		operation.doCreateOrUpdate();
		operation.postCreateOrUpdate();
	}



	public void removeTopic(final Integer topicId) {
		txRunner.doInTransaction(new ITransactionOperation() {
			@Override
			public Void run() {
				topicMapper.deleteByTopicId(topicId);
				topicRedis.execute(new RedisOperation(){
					@Override
					public Void doWithRedis(Jedis jedis) {
						Transaction tx = jedis.multi();
						String topicKey = TopicRedis.topicKey(topicId);
						tx.del(topicKey);
						tx.hdel(TopicRedis.topicNamesMap(), 
								TopicRedis.topicNameField(topicId, "en"),TopicRedis.topicNameField(topicId, "zh"));
						tx.exec();
						return null;
					}
						
				});
				return null;
			}

		});
	}

	private class CreateOrUpdateOperation {
		private final Topic topic;
		private final TopicDetail topicDetail;
		private final List<Runnable> postRunnables = Lists.newArrayList();

		private CreateOrUpdateOperation(Topic topic,TopicDetail topicDetail){
			this.topic = topic;
			this.topicDetail = topicDetail;
		}

		public void doCreateOrUpdate() throws IOException {
			final Topic oldTopic = getTopicDetail(topic.getId(),topicDetail.getLang());
			final TopicDetail oldTopicDetail = oldTopic != null && oldTopic.getTopicDetails()!= null ?
					oldTopic.getTopicDetails().iterator().next():null;
					
			String oldCorpLogo = null;
			String oldVideo = null;
			String oldProfile = null;
			String oldAttachment = null;
			
			String newCorpLogo = null;
			String newVideo = null;
			String newProfile = null;
			String newAttachment = null;
			
			
			Collection<String> oldTopicIntroduceImages=Collections.emptyList();;
			
			if(oldTopic != null){
				oldCorpLogo = oldTopic.getCorpLogo();
				oldVideo = oldTopic.getVideo();
				oldProfile =oldTopic.getProfile();
			}
			
			if (oldTopicDetail != null) {
				oldAttachment = oldTopicDetail.getAttachment();
				oldTopicIntroduceImages = Collections2.transform(oldTopicDetail.getTopicIntroduces(),topicIntroduceImageFunction);
			}

			newCorpLogo = toLocalHttpFile(oldCorpLogo, topic.getCorpLogo(),topic.getOwnAccountId());
			topic.setCorpLogo(newCorpLogo);
			newVideo = toLocalHttpFile(oldVideo,topic.getVideo(),topic.getOwnAccountId());
			topic.setVideo(newVideo);
			newProfile = toLocalHttpFile(oldProfile,topic.getProfile(),topic.getOwnAccountId());
			topic.setProfile(newProfile);
			
			newAttachment = toLocalHttpFile(oldAttachment,topicDetail.getAttachment(), topic.getOwnAccountId());
			topicDetail.setAttachment(newAttachment);
			
			
			
			if(topicDetail.getTopicIntroduces()!= null && !topicDetail.getTopicIntroduces().isEmpty())
				for(TopicIntroduce introduce: topicDetail.getTopicIntroduces()){
					String image = toLocalHttpFile(null,introduce.getImage(),topic.getOwnAccountId());
					introduce.setImage(image);
				}

			txRunner.doInTransaction(new ITransactionOperation() {
				@Override
				public Void run() {
					if (topic.getId() == null) {
						topic.setArtifactAmount(0);
						topic.setCreateTime(new Date());
						topicMapper.insertTopic(topic);
						topicMapper.insertTopicDetail(topic.getId(), topicDetail);
						topicMapper.insertTopicIntroduces(topic.getId(), topicDetail.getLang(), topicDetail.getTopicIntroduces());
					} else {
						topicMapper.updateTopic(topic);
						if(oldTopicDetail != null){
							topicMapper.updateTopicDetail(topic.getId(), topicDetail);
							topicMapper.deleteTopicIntroductionByTopicIdAndLang(topic.getId(), topicDetail.getLang());
						}
						else{
							topicMapper.insertTopicDetail(topic.getId(), topicDetail);
						}
						topicMapper.insertTopicIntroduces(topic.getId(), topicDetail.getLang(), topicDetail.getTopicIntroduces());
					}
					topicRedis.execute(new RedisOperation(){
						@Override
						public Void doWithRedis(Jedis jedis) {
							String topicKey = TopicRedis.topicKey(topic.getId());
							jedis.watch(topicKey);
							Transaction tx = jedis.multi();
							tx.hset(TopicRedis.topicNamesMap(), TopicRedis.topicNameField(topic.getId(), topicDetail.getLang()), topicDetail.getTitle());
							tx.hmset(topicKey, TopicRedis.toStringMap(topic));
							String topicDetailJson = JsonUtil.toJsonString(topicDetail);
							tx.hset(topicKey, TopicRedis.topicDetailLangKey(topicDetail.getLang()), topicDetailJson);
							tx.exec();
							return null;
						}
						
					});
					return null;
				}

			});
			postRunnables.add(
					new TopicDetailBcsAddFileRunnable(oldProfile,newProfile,new UpdateProfileFutureCallback(topic.getId(),newProfile)));
			postRunnables.add(
					new TopicDetailBcsAddFileRunnable(oldVideo,newVideo,new UpdateVideoFutureCallback(topic.getId(),newVideo)));
			postRunnables.add(
				new TopicDetailBcsAddFileRunnable(oldCorpLogo,newCorpLogo,new UpdateCorpLogoFutureCallback(topic.getId(),newCorpLogo)));
			postRunnables.add(
				new TopicDetailBcsAddFileRunnable(oldAttachment,newAttachment,new UpdateAttachmentFutureCallback(topic.getId(),topicDetail.getLang(),newAttachment)));
			postRunnables.add(new TopicIntroduceBcsAddFileRunnable(topic.getId(),topicDetail.getLang(),topicDetail.getTopicIntroduces(),oldTopicIntroduceImages));
		}
		
		private void postCreateOrUpdate(){
			for(Runnable runnable:postRunnables){
				runnable.run();
			}
		}
		
	}

	

	private class TopicIntroduceBcsAddFileRunnable implements Runnable{
		private final Integer topicId;
		private final String lang;
		private final Collection<TopicIntroduce> introduces;
		private final Collection<String> oldImages;
		public TopicIntroduceBcsAddFileRunnable(Integer topicId, String lang,
				Collection<TopicIntroduce> introduces,Collection<String> oldImages) {
			super();
			this.topicId = topicId;
			this.lang = lang;
			this.introduces = introduces;
			this.oldImages = oldImages;
		}
		
		public void run(){
			final List<ListenableFuture<TopicIntroduce>> futures = Lists.newArrayListWithCapacity(introduces.size());
			for(final TopicIntroduce introduce:introduces){
				if(oldImages.contains(introduce.getImage())){
					oldImages.remove(introduce.getImage());
					final ListenableFuture<TopicIntroduce> future = MoreExecutors.sameThreadExecutor().submit(new Callable<TopicIntroduce>(){
							public TopicIntroduce call() throws Exception {
								return introduce;
							}});
					futures.add(future);
				}else if(LotusprizeLocalFiles.instance().isLocalFile(introduce.getImage())){
					final ListenableFuture<TopicIntroduce> future = AppListeningExecutorService.instance().executor().submit(
						new Callable<TopicIntroduce>(){
							public TopicIntroduce call() throws Exception {
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
			ListenableFuture<List<TopicIntroduce>> introducesFuture = Futures.allAsList(futures);
			Futures.addCallback(introducesFuture, new FutureCallback<List<TopicIntroduce>>(){
				@Override
				public void onSuccess(final List<TopicIntroduce> result) {
					txRunner.doInTransaction(new ITransactionOperation(){
						@Override
						public Void run() {
							for(TopicIntroduce introduce:result){
								topicMapper.updateTopicIntroductImage(topicId, lang, introduce.getPos(), introduce.getImage());
							}
							topicRedis.execute(new RedisOperation(){
								@Override
								public Void doWithRedis(Jedis jedis) {
									String topicKey = TopicRedis.topicKey(topicId);
									String topicDetailkey = TopicRedis.topicDetailLangKey(lang);
									String topicDetailJson = jedis.hget(topicKey,topicDetailkey);
									TopicDetail topicDetail = JsonUtil.fromJson(topicDetailType, topicDetailJson);
									topicDetail.setTopicIntroduces(result);
									jedis.hset(topicKey,topicDetailkey,JsonUtil.toJsonString(topicDetail));
									return null;
								}
							});
							return null;
						}
					});
					for(String oldImage:oldImages){
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
	
	
	
	private class TopicDetailBcsAddFileRunnable implements Runnable{
		private final String oldFileUri;
		private final String newFileUri;
		private final FutureCallback<String> futureCallback;

		public TopicDetailBcsAddFileRunnable(String oldFileUri,
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
								if(bcsRemoveFileEvent.getEx() != null){
									deleteBcsFileHandler.deleteOnFailure(bcsRemoveFileEvent.getEx(), oldFileUri);
								}else{
									deleteBcsFileHandler.deleteOnSuccess(bcsRemoveFileEvent.getResult(),oldFileUri);
								}
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
	
	
	
	private class UpdateAttachmentFutureCallback implements	FutureCallback<String> {

		private final Integer topicId;
		
		private final String lang;

		private final String originalAttachment;

		public UpdateAttachmentFutureCallback(Integer topicId,String lang,String originalAttachment) {
			super();
			this.topicId = topicId;
			this.lang = lang;
			this.originalAttachment = originalAttachment;
		}

		@Override
		public void onSuccess(final String attachment) {
			txRunner.doInTransaction(new ITransactionOperation() {
				@Override
				public Void run() {
					topicMapper.updateTopicDetailAttachment(topicId,lang, attachment);
					topicRedis.execute(new RedisOperation(){
						@Override
						public Void doWithRedis(Jedis jedis) {
							String topicKey = TopicRedis.topicKey(topicId);
							String topicDetailkey = TopicRedis.topicDetailLangKey(lang);
							String topicDetailJson = jedis.hget(topicKey,topicDetailkey);
							TopicDetail topicDetail = JsonUtil.fromJson(topicDetailType, topicDetailJson);
							topicDetail.setAttachment(attachment);
							jedis.hset(topicKey,topicDetailkey,JsonUtil.toJsonString(topicDetail));
							return null;
						}
						
					});
					return null;
				}
			});
		}

		@Override
		public void onFailure(Throwable t) {
			logger.error("上传附件至百度云存储错误,topicId:" + topicId + ",lang:"+lang+ ",attachment:"
					+ originalAttachment, t);
		}

	}

	private class UpdateCorpLogoFutureCallback implements FutureCallback<String> {

		private final Integer topicId;
		

		private final String originalCorpLogo;

		public UpdateCorpLogoFutureCallback(Integer topicId,String originalCorpLogo) {
			super();
			this.topicId = topicId;
			this.originalCorpLogo = originalCorpLogo;
		}

		@Override
		public void onSuccess(final String corpLogo) {
			txRunner.doInTransaction(new ITransactionOperation() {
				@Override
				public Void run() {
					topicMapper.updateCorpLogo(topicId,corpLogo);
					topicRedis.execute(new RedisOperation(){
						@Override
						public Void doWithRedis(Jedis jedis) {
							String topicKey = TopicRedis.topicKey(topicId);
							jedis.hset(topicKey,"corpLogo",corpLogo);
							return null;
						}
					});
					return null;
				}
			});

		}

		@Override
		public void onFailure(Throwable t) {
			logger.error("上传企业Logo至百度云存储错误,topicId:" + topicId + ",corpLogo:"
					+ originalCorpLogo, t);
		}

	}
	
	
	private class UpdateProfileFutureCallback implements FutureCallback<String> {

		private final Integer topicId;

		private final String originalProfile;

		public UpdateProfileFutureCallback(Integer topicId,String originalProfile) {
			super();
			this.topicId = topicId;
			this.originalProfile = originalProfile;
		}

		@Override
		public void onSuccess(final String profile) {
			txRunner.doInTransaction(new ITransactionOperation() {
				@Override
				public Void run() {
					topicMapper.updateProfile(topicId, profile);
					topicRedis.execute(new RedisOperation(){
						@Override
						public Void doWithRedis(Jedis jedis) {
							String topicKey = TopicRedis.topicKey(topicId);
							jedis.hset(topicKey,"profile",profile);
							return null;
						}
					});
					return null;
				}
			});

		}

		@Override
		public void onFailure(Throwable t) {
			logger.error("上传 profile 至百度云存储错误,topicId:" + topicId +",profile:"
					+ originalProfile, t);
		}

	}
	
	private class UpdateVideoFutureCallback implements FutureCallback<String> {

		private final Integer topicId;

		private final String originalVideo;

		public UpdateVideoFutureCallback(Integer topicId,String originalVideo) {
			super();
			this.topicId = topicId;
			this.originalVideo = originalVideo;
		}

		@Override
		public void onSuccess(final String video) {
			txRunner.doInTransaction(new ITransactionOperation() {
				@Override
				public Void run() {
					topicMapper.updateVideo(topicId, video);
					topicRedis.execute(new RedisOperation(){
						@Override
						public Void doWithRedis(Jedis jedis) {
							String topicKey = TopicRedis.topicKey(topicId);
							jedis.hset(topicKey,"video",video);
							return null;
						}
					});
					return null;
				}
			});

		}

		@Override
		public void onFailure(Throwable t) {
			logger.error("上传 profile 至百度云存储错误,topicId:" + topicId +",video:"
					+ originalVideo, t);
		}

	}
	
	
	private class DeleteBcsFileHandler {

		public void deleteOnSuccess(String result,String bcsObjectPath) {

		}

		public void deleteOnFailure(Throwable t,String bcsObjectPath) {
			logger.error("删除百度云存储文件错误,bcsObjectPath:" + bcsObjectPath, t);
		}

	}
	
	

}
