<template>
	<view>
		<view class="actions">
			<view class="actheader w">
				<img :src="item.user.avatarUrl" v-if="item" class="userimg">
				<view class="actname" v-if="item.bottomActivityInfos">{{item.user.nickname}} </view>
				<view class="_action" v-if="item">
					{{item.typeDesc!==null?item.typeDesc:item.info.commentThread.resourceTitle}}</view>
			</view>
			<view class="_bodytext w">
				<view class="msg">
					{{item.json.msg}}
				</view>
				<text class="link" v-if="item.bottomActivityInfos">
				<a :href="item.bottomActivityInfos[0]!==null?item.bottomActivityInfos[0].h5Target:blank">#2{{item.bottomActivityInfos!==null?item.bottomActivityInfos[0].name:null}}</a>
				</text>
				<text class="tibs" v-if="item.bottomActivityInfos">
					<a v-if="item.bottomActivityInfos[1]"
						:href="item.bottomActivityInfos[1]!==null?item.bottomActivityInfos[1].h5Target:blank">#{{item.bottomActivityInfos[1]!==null?item.bottomActivityInfos[1].name:null}}#</a>
				</text>
			</view>
			<view class="_body w">
				<!-- <img v-if="item.pics[0]" :src="item.pics[0].pcSquareUrl" class="squareimg"> -->
				<view class="imgs" style="display: inline-block;" v-if="item.pics[0]">
					<img v-for="item2 in item.pics" :src="item2.pcSquareUrl" class="squareimg"  :width="item2.width/10" :height="item2.height/10">
				</view>
				<view class="audio" v-if="item.json.song">
					<img :src="item.json.song.album.blurPicUrl">
					<view class="_song">
						<p class="songname">{{item.json.song.name}}{{item.json.song.alias[0]}}</p>
						<text class="songAuther">{{item.json.song.artists[0].name}}</text>
					</view>
				</view>
				<view class="audio" v-if="item.json.album">
					<img :src="item.json.album.blurPicUrl">
					<view class="_song">
						<p class="songname">{{item.json.album.name}}{{item.json.album.alias[0]}}</p>
						<text class="songAuther"> || {{item.json.album.artists[0].name}}</text>
					</view>
				</view>
				<video src="" v-if="item.json.resource" :poster="item.json.resource.coverUrl"></video>
			</view>
				<view class="_footer" v-if="item">
					<view class="dis" :class="current==0?'active':''" @tap="chgcrrt(0)">
						评论{{item.info.commentCount}}
					</view>
					<view :class="current==2?'active':''" @tap="chgcrrt(2)" class="dz">
						点赞{{item.info.likedCount}}
					</view>
					<!-- <view :class="current==1?'active':''" @tap="chgcrrt(1)">
						转发{{item.info.shareCount}}
					</view> -->
				</view>
				<view v-if="current==0" class="w">
					<h3>精彩评论：</h3>
					<view class="cmt" v-for="item in _comments.comments" :key="item.userId">
						<view class="cmtuser">
							<img :src="item.user.avatarUrl">
							<view class="user">
								<text class="username">{{item.user.nickname}}</text>
								<text class="cmttime">{{item.timeStr}}</text>
							</view>
						</view>
						<view class="user_cmt">
							{{item.content}}
						</view>
					</view>
				</view>
				<!-- <view v-if="current==1">
					<h3>转发：</h3>
				</view> -->
				<view v-if="current==2">
					<h3 style="margin-left: 5%;">点赞：</h3>
					<view class="cmt" v-for="item in liked">
						<view class="likedlist">
							<img :src="item.profile.avatarUrl">
							<view class="likeduser">
								<text class="username2">{{item.profile.nickname}}</text>
							</view>
						</view>
					</view>
				</view>
				<view class="input">
					<input v-model="mycmt" type="text" placeholder="写评论"><!-- <button class="btn" >写评论</button> -->
				</view>
				<button class="send" @tap="sendcmt()">发送</button>
		</view>
	</view>
</template>

<script>
	import {
		sendActCmt,getlikedusers,msgSecCheck,getTaken
	} from "../../utils/request.js"
	export default {
		data() {
			return {
				item: {},
				_comments: {},
				liked:[],
				mycmt: "",
				current: 0,
				taken:"",
				code:'',
				openid:'',
				result:{}
			}
		},
		onLoad() {
			this.openid = uni.getStorageSync("openid")
			this.item = JSON.parse(uni.getStorageSync("item"))
			this._comments = JSON.parse(uni.getStorageSync("detail"))
			this.item.info.commentThread.latestLikedUsers?.map(item =>
					getlikedusers(item.s,res =>{
						this.liked.push(res)
					})
				)
				// uni.login({
				//   success (res) {
				//     if (res.code) {
				//       //发起网络请求
				//       this.code = res.code
				// 	  console.log(res.code,111)
				//     } else {
				//       console.log('登录失败！' + res.errMsg)
				//     }
				//   }
				// })
			getTaken( 'wx90d3328edfe8f704','7aa7c0374dc3af309e5e8629d2ff531c',res =>{
						this.taken = res.access_token
						console.log(res)
					})
		},
		methods: {
			backact() {
				setTimeout(() => {
					uni.reLaunch({
						url: "./square"
					});
				}, 1000);
			},
			sendcmt() {
				msgSecCheck(
				this.taken,
				{
				       "openid": this.openid,
				       "scene": 2,
				       "version": 2,
				       "content":this.mycmt
				   },
				   res =>{
					   this.result = res.result
					   switch(this.result.label){
						   case 100:sendActCmt(this.item.info.threadId, this.mycmt, res => {
						});
						   console.log("正常");
									break;
						   case 10001:console.log("广告");
							break;
						   case 20001:console.log("政治敏感话题");
							break;
						   case 20002:console.log("包含色情信息");
							break;
						   case 20003:console.log("包含辱骂语句");
							break;
						   case 20006:console.log("违法犯罪信息");
							break;
						   case 20008:console.log("欺诈信息");
							break;
						   case 20008:console.log("低俗信息");
							break;
						   case 20008:console.log("版权问题");
							break;
						   case 20008:console.log("其他");
							break;
							default:
					   }
					   // console.log(res,222)
				   },res =>{
					   // console.log(res,333)
				   }
				)
			},
			chgcrrt(x) {
				this.current = x
			}
		}
	}
</script>

<style>
.imgs{
	width: 100%;
	display: flex;
	flex-wrap: wrap;
}
.imgs>img{
	border-radius: 3%;
	box-shadow: 2rpx 2rpx 2rpx lightgray;
	margin: 5rpx;
	min-width: 200rpx;
	max-height: 30vw;
}
.body{
	width: 90%;
	margin: 0 auto;
	background-color: #F5F5F5;
}
.w{
	width: 90%;
	margin: 0 auto;
}
._body{
}
.userimg{
	margin: 10rpx;
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
}
.msg{
	font-size: 16px;
	letter-spacing: 2px;
}
.link a{
	color: #648CB7;
	font-size: 18px;
	letter-spacing: 3px;
}
.tibs a{
	color: #648CB7;
	font-size: 18px;
	letter-spacing: 3px;
}
.actions{
	width: 98%;
	min-height: 100%;
	margin: 10rpx auto;
}
.actname{
	display: inline-block;
	padding: 10rpx;
	vertical-align: top;
	width: auto;
	font-size: 18px;
	font-weight: 800;
}
._action{
	display: inline-block;
	padding-bottom: 65rpx;
	width: 160rpx;
	overflow: hidden;
    white-space: nowrap;
	font-size: 18px;
	color: #8b8b8b;
}
.audio{
	width: 93%;
	height: 100rpx;
	padding: 20rpx;
	margin: 10px 0;
	background-color: #e9e9e9;
	border-radius: 10px;
}
.audio img{
	width: 100rpx;
	height: 100rpx;
	border-radius: 10px;
	float: left;
}
._song{
	width: 300px;
	margin-left: 60px;
}
._song .songname {
	font-size: 18px;
	width: 200px;
	white-space: nowrap;
	overflow:hidden;
	text-overflow: ellipsis;
}
._song .songAuther{
	font-size: 14px;
	color: #7f7f7f;
}
.squareimg{
	width: 100rpx;
	border-radius: 1%;
}
video{
	width: 350rpx;
	height: 300rpx;
	border: 1rpx gray solid;
	display: block;
	margin: 10rpx auto;
}
h3{
	letter-spacing: 4px;
}
.footer>button{
	width: 100rpx;
	height: 50rpx;
	font-size: 10rpx;
	padding: 0;
}
	._footer {
		width: 100%;
		margin-left: 10%;
	}

	._footer>* {
		margin: 20rpx auto;
		display: inline-block;
	}
	.dis{
		margin: 20rpx auto;
		display: inline-block;
	}
	.dz{
		margin: 20rpx auto;
		display: inline-block;
	}
.dis{
	margin-right: 20px;
}
	.active {
		border-bottom: 5rpx red solid;
	}
.likedlist>img{
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	float: left;
}
.likeduser{
	height: 110rpx;
	justify-content: center;
	font-size: 100%;
}
.cmt{
	width: 90%;
	padding: 20rpx;
	border-bottom: 1rpx #F0F0F0 solid;;
}
.cmtuser>img{
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	float: left;
}
.user{
	width: 280px;
	margin-left: 60px;
}
.username{
	color: #7e7e7e;
	display: block;
	margin-bottom: 5px;
}
.username2{
	display: inline-block;
	margin-top: 5%;
	margin-left: 5%;
}
.cmttime{
	color: #7e7e7e;
}
.user_cmt{
	margin-left: 60px;
	font-size: 18px;
	margin-top: 20rpx;
}
.likedlist>img{
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	float: left;
}
.likeduser{
	height: 110rpx;
	justify-content: center;
	font-size: 100%;
}
.footer>input{
	width: 100%;
	/* margin: 40rpx; */
}
.input {
	border-top: #f5f5f5 solid 1px;
		width: 95%;
		height: 80rpx;
		/* border-radius: 1em; */
		overflow: hidden;
		position: fixed;
		bottom: 0px;
		/* left: 2%; */
		background-color: white;
		padding-left: 20rpx;
	}

	input {
		margin: auto auto;
		display: inline-block;
		height: 80rpx;
	}
.send {
		position: fixed;
		font-size: 32rpx;
		letter-spacing: 5rpx;
		height: 80rpx;
		width: 200rpx;
		bottom: 0rpx;
		right: 10rpx;
	}
</style>
