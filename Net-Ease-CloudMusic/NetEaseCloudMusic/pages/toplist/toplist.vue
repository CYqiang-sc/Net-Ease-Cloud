<template>
	<view class="content">
		<view class="header">
			<text :class="{'active': isofficial}" @click="gotoofficial()">官方榜</text>
			<text :class="{'active': !isofficial}" @click="gototop()">推荐榜</text>
		</view>
		<view class="top-body">
			<text class="title">官方榜</text>
			<view v-for="(officialitem,officialindex) in OfficialList" :key="officialindex">
				<view class="official-item">
					<navigator
						:url="'/pages/toplistdetail/toplistdetail?id='+OfficialId[officialindex]+'&name='+OfficialName[officialindex]">
						<image :src="OfficialImg[officialindex]"></image>
						<view class="official-song">
							<view v-for="(nameitem,nameindex) in officialitem" :key="nameindex">
								<view class="every-song">
									<text class="num">{{nameindex+1}}</text>
									<text class="first">{{nameitem.first}}</text>
									<text class="second">- {{nameitem.second}}</text>
								</view>
							</view>
						</view>
					</navigator>
				</view>
			</view>
			<text class="title">推荐榜</text>
			<view v-for="(item,index) in topList" :key="index">
				<!-- 其他推荐榜单 -->
				<view v-if="!item.ToplistType" class="toplist-button">
					<navigator :url="'/pages/toplistdetail/toplistdetail?id='+item.id+'&name='+item.name"
						hover-class="navigator-hover">
						<image :src="item.coverImgUrl" style="width: 200rpx; height: 200rpx; border-radius: 20rpx;">
						</image>
					</navigator>
					<view class="toplist-update">{{item.updateFrequency}}</view>
					<text class="toplist-text">{{item.name}}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getTopList
	} from "../../utils/request.js"
	export default {
		data() {
			return {
				topList: [],
				OfficialList: [],
				OfficialImg: [],
				OfficialId: [],
				OfficialName: [],
				isofficial: true
			};
		},
		onPageScroll(res) {
			//获取距离顶部距离
			const scrollTop = res.scrollTop;
			if (scrollTop >= 500) {
				this.isofficial = false;
			}else{
				this.isofficial = true;
			}
			console.log(scrollTop)
		},
		onLoad() {
			getTopList(res => {
				console.log(res);
				if (res.code == 200) {
					console.log(res.list);
					this.topList = res.list;
					this.topList.map(item => {
						if (item.ToplistType) {
							this.OfficialList.push(item.tracks); //获取官方榜歌曲
							this.OfficialImg.push(item.coverImgUrl) //官方榜的图片
							this.OfficialId.push(item.id) //官方榜的id
							this.OfficialName.push(item.name) //官方榜的name
						}
					})
					console.log(this.OfficialList[0][0].first);
				}
			})
		},
		methods: {
			gotoofficial() {
				uni.pageScrollTo({
					scrollTop: 0,
					duration: 300
				});
				this.isofficial = true;
			},
			gototop() {
				uni.pageScrollTo({
					scrollTop: 500,
				 duration: 300
				});
				this.isofficial = false;
			}
		}
	}
</script>

<style lang="scss">
	.header {
		position: fixed;
		top: 80rpx;
		z-index: 99;
		background-color: white;
		height: 90rpx;
		line-height: 90rpx;
		width: 100%;
		text-align: center;
	}

	.header text {
		margin: 0 100rpx;
		font-size: 34rpx;
		font-weight: 600;
	}

	.top-body {
		position: relative;
		top: 60rpx;
	}

	.active {
		font-size: 36rpx;
		color: #D81E06;
		border-bottom: 2px solid #D81E06;
	}

	.top-body .title {
		display: block;
		font-size: 40rpx;
		font-weight: 700;
		margin-left: 30rpx;
		margin-bottom: 20rpx;
		margin-top: 30rpx;
	}

	// 官方榜
	.official-item {
		position: relative;
		display: inline-block;
		margin-left: 37.5rpx;
		margin-right: 37.5rpx;
		margin-top: 11rpx;
		margin-bottom: 11rpx;
		height: 200rpx;
	}

	.official-item image {
		width: 200rpx;
		height: 200rpx;
		border-radius: 20rpx;
	}

	.official-song {
		padding: 5rpx 0;
		// display: inline-block;
		float: right;
		line-height: 65rpx;
		width: 400rpx;
		margin-left: 30rpx;
	}

	.every-song {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		display: block;
	}

	.every-song .num {
		font-weight: 700;
		margin-right: 20rpx;
	}

	.every-song .first {}

	.every-song .second {
		font-size: 16rpx;
		color: #707070;
		margin-left: 20rpx;
	}

	// 榜单按钮
	.toplist-button {
		position: relative;
		float: left;
		width: 200rpx;
		height: 280rpx;
		margin-left: 37.5rpx;
		border-radius: 20rpx;
		text-align: center;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.toplist-update {
		position: absolute;
		padding: 0 8rpx;
		top: 10rpx;
		right: 5rpx;
		font-size: 22rpx;
		line-height: 35rpx;
		color: #f4f4f4;
		border-radius: 30rpx;
		background-color: rgba(233, 233, 233, 0.3);
	}

	.toplist-text {
		font-size: 30rpx;
		height: 30rpx;
		line-height: 30rpx;
	}
</style>
