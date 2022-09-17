<template>
	<view class="search">
		<!-- 搜索栏 -->
		<view>
			<uni-search-bar maxlength="15" :placeholder="placeholderdefault" v-model="searchValue" cancelButton="none" bgColor="#EEEEEE" radius="50" @confirm="barSearch" @clear="clear"/>
		</view>
		<block v-if="searchList.length">
			<view class="show-search-result">
				<view class="search-result">搜索内容：</view>
				<view class="search-list">
					<view class="search-detail-list" v-for="(item,index) in searchList" :key="index">
						<view class="search-list-icon">
							<slot name="searchIcon">
								<uni-icons color="#424242" size="18" type="search" />
							</slot>
						</view>
						<text class="search-list-content" @click="gotosong(item)">{{item.name}}</text>
						<text class="search-list-singer">-{{item.artists[0].name}}</text>
					</view>
				</view>
			</view>
		</block>
		<block v-else>
			<!-- 历史记录 -->
			<view class="history" v-if="historyList.length">
				<view class="history-title">历史</view>
				<scroll-view scroll-x enable-flex show-scrollbar="false" class="history-scroll">
					<view class="history-list" v-for="(item,index) in historyList" :key="index" @click="historyToSearch(item)">
						{{item}}
					</view> 
				</scroll-view>
				<text class="iconfont icon-shanchu delete" @click="deletehistory"></text>
			</view>
			<view class="hot-search">
			    <view class="title">热搜榜</view>
			    <!-- 热搜列表 -->
			    <view class="hot-search-list">
					<view class="hot-list" v-for="(item,index) in hotList" :key="index" @click="hotToSearch(item.searchWord)">
						<text class="hot-list-num">{{index + 1}}</text>
						<text class="hot-list-word">{{item.searchWord}}</text>
						<image class="hot-list-icon" v-if="item.iconUrl" :src="item.iconUrl"></image>
					</view>
			     </view>
			</view>
		</block>
	</view>
</template>

<script>
	import uniSearchBar from "../../components/uni-search-bar/uni-search-bar.vue"
	import {getSearchDefault,getHotList,getSearchList} from "../../utils/request.js"
	export default {
		components: {
			uniSearchBar
		},
		data() {
			return {
				searchValue: '',
				placeholderdefault: '',
				searchContent: '',
				searchList: [],
				historyList: [],
				hotList: []
			}
		},
		onShow() {
			if( uni.getStorageSync('searchHistory')){
			this.historyList = uni.getStorageSync('searchHistory');
			console.log(uni.getStorageSync('searchHistory'));
			}
		},
		onLoad() {
			getSearchDefault(res=>{
				console.log(res);
				if(res.code==200){
					console.log(res.data.showKeyword);
					this.placeholderdefault = res.data.showKeyword
				}
			})
			getHotList(res=>{
				console.log(res);
				if(res.code==200){
					console.log(res.data);
					this.hotList = res.data;
				}
			})
			
		},
		methods: {
			// 输入栏搜索
			barSearch(res) {
				if(!res.value){
					this.searchList=[];
					return;
				}
				this.setHistoryList(res.value);
				console.log(res);
				this.searchContent=res.value;
				getSearchList(this.searchContent,res=>{
					console.log(res)
					this.searchList = res.result.songs;
				})
			},
			search(res) {
				this.setHistoryList(res);
				console.log(res);
				this.searchContent=res;
				getSearchList(this.searchContent,res=>{
					console.log(res)
					this.searchList = res.result.songs;
				})
			},
			clear(res){
				this.searchList=[];
			},
			// 热搜榜搜索
			hotToSearch(value){
				this.searchValue=value;
				this.search(value);
			},
			// 热搜榜搜索
			historyToSearch(value){
				this.searchValue=value;
				this.search(value);
			},
			// 存储搜索历史
			setHistoryList(value) {
				console.log(this.historyList);
				// 判断搜索是否重复
				if (this.historyList.length > 0) {
					let flag = false;
					for (let i of this.historyList) {
						if (i == value) {
							flag = false;
							return;
						} else {
							flag = true;
						}
					}
					if (flag) {
						this.historyList.unshift(value);
					}
				} else {
					console.log(value);
					this.historyList.unshift(value);
				}
				uni.setStorageSync('searchHistory',this.historyList);
			},
			// 获取本地历史记录
			getHistoryList(){
				if(this.historyList!== undefined && this.historyList!== null && this.historyList.length>0){
					this.historyList = uni.getStorageSync('searchHistory');
					console.log(uni.getStorageSync('searchHistory'));
				}
			},
			deletehistory(){
				this.historyList = [];
				uni.setStorageSync('searchHistory',[]);
			},
			gotosong(item){
				this.addcurrentsong(item.id);
				uni.navigateTo({
					url:'/pages/song/song?id='+item.id+'&name='+item.name
				})
			},
			// 添加到播放列表
			addcurrentsong(id) {
				// this.currentSongid=id;
				console.log(id);
				const currentsong = uni.getStorageSync('currentsong') || [];
				let flag = 1; //判断是否在播放列表中
				currentsong.map(item => {
					if (item == id) {
						flag = 0;
					}
				})
				if (flag == 1) {
					uni.showToast({
						title: "添加成功",
						icon: "success",
						mask: true,
					})
					currentsong.unshift(id);
					uni.setStorageSync('currentsong', currentsong);
				}
				uni.setStorageSync('currentnum', 0)
				uni.setStorageSync('currentid', id)
			
			},
		}
	}
</script>

<style lang="scss">
	.search{
		padding: 0 20rpx;
	}
	// 搜索结果列表
	.search-result{
		height: 80rpx;
		line-height: 80rpx;
		font-size: 32rpx;
		color: #d43c33;
		border-bottom: 1rpx solid #d43c33;
	}
	.search-detail-list{
		height: 80rpx;
		line-height: 80rpx;
		display: flex;
		flex-direction: row;
		border-bottom: 1rpx solid #eee;
		
		text-overflow: ellipsis;
	}
	.search-list-content{
		flex: 1;
		margin-left: 20rpx;
		max-width: 450rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		font-size: 30rpx;
	}
	.search-list-singer{
		font-size: 29rpx;
		max-width: 200rpx;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	//历史数据
	.history {
		position: relative;
		display: flex;
		flex-wrap: wrap;
		line-height:  50rpx;
		font-size: 34rpx;
		margin-top: 20rpx;
	}
	.history-title{
		font-size: 30rpx;
		height: 50rpx;
	}
	.history-scroll{
		width: 80%;
		padding: 0 20rpx;
		white-space: nowrap;

	}
	.history-list{
		display: inline-block;
		height: 50rpx;
		font-size: 26rpx;
		background: #ededed;
		margin-left: 20rpx;
		padding: 0 30rpx;
		border-radius: 20rpx;
		margin-bottom: 20rpx;
	}
	.history .delete {
		bottom: 10rpx;
		font-size: 36rpx;
	}
	/* 热搜榜 */
	.hot-search {
		margin-top: 30rpx;
	}
	.hot-search .title {
		font-size: 34rpx;
		height: 80rpx;
		line-height: 80rpx;
		border-bottom: 1rpx solid #ccc;
	}
	.hot-search-list{
		display: flex;
		flex-wrap: wrap;
		position: relative;
	}
	.hot-list {
		width: 50%;
		height: 80rpx;
		line-height: 80rpx;
		display: inline-block;
		font-size: 26rpx;
		text-overflow: ellipsis;
	}
	.hot-list .hot-list-num {
		margin: 0 10rpx;
		width: 10%;
		float: left;
	}
	.hot-list .hot-list-word{
		// align-items: left;
		text-overflow: ellipsis;
		// color: #d43c33;
		// padding-right: 20rpx;
		width: 10%;
	}
	.hot-list .hot-list-icon {
		// width: auto;
		max-width: 35rpx;
		max-height: 20rpx;
		margin-left: 10rpx;
		margin-top: 28rpx;
		position: absolute;
		
	}
</style>
