<template>
	<view>
		<!-- 关注列表 -->
		<view class="attention">
			<view class="left">
				<view class="attent">
					<view class="mycircle">
					<img src="../../static/icon/artist.png" class="">
				</view>
					<view>我的圈子</view>
				</view>
			</view>
			<view class="right">
				<scroll-view scroll-x="true" scroll-y="false">
				<view class="attent" v-for="(item,index) in attenlist" :key="index">
					<view class="attenlist">
						<img :src="item.avatarUrl">
					</view>
					<view class="attenname">{{item.nickname}}</view>
				</view>
				<view class="attent" @tap="allattent">
					<view class="attenlist">
						<uni-icons type="more-filled" size="50"></uni-icons>
					</view>
					<view>全部</view>
				</view>
			</scroll-view>
			</view>
			
		</view>
		
		<!-- 动态 -->
		<view class="actions" v-for="(item,index) in acterlist.event" :key="item.id" @tap="getdetail(item,item.info.threadId)">
			<view class="actheader" v-if="item" >
				<img :src="item.user.avatarUrl"class="userimg">
				<view class="actname">{{item.user.nickname}} </view>
				<view class="_action" v-if="item">{{item.typeDesc!==null?item.typeDesc:(item.info.commentThread.resourceTitle?item.info.commentThread.resourceTitle:"转发：")}}</view>
			</view>
			<view class="_bodytext" v-if="item">
				<view class="msg">
					{{item.json.msg}}
				</view>
				<text class="link" v-if="item.bottomActivityInfos">
				<a :href="item.bottomActivityInfos[0]!==null?item.bottomActivityInfos[0].h5Target:blank">#2{{item.bottomActivityInfos!==null?item.bottomActivityInfos[0].name:null}}</a>
				</text>
				<view class="one">
					<text class="tibs" v-if="item.bottomActivityInfos">
						<a v-if="item.bottomActivityInfos[1]" :href="item.bottomActivityInfos[1]!==null?item.bottomActivityInfos[1].h5Target:blank">#{{item.bottomActivityInfos[1]!==null?item.bottomActivityInfos[1].name:null}}#</a>
					</text>
				</view>
				
			</view>
			<view class="_body" v-if="item">
				<view class="imgs" style="display: inline-block;" v-if="item.pics[0]">
					<img v-for="item2 in item.pics" :src="item2.pcSquareUrl" class="squareimg" >
				</view>
				<view class="audio" v-if="item.json.song" @tap.native.stop="toSong(item.json.song.id,item.json.song.name)" >
					<img :src="item.json.song.album.blurPicUrl">
					<view class="_song">
					    <p class="songname">{{item.json.song.name}}{{item.json.song.alias[0]}}</p>
					    <text class="songAuther">{{item.json.song.artists[0].name}}</text>
				    </view>
				</view>
				<view class="album" v-if="item.json.album">
					
				</view>
				<view class="audio" v-if="item.json.album" >
					<img :src="item.json.album.blurPicUrl">
					<view class="_song">
						<p class="songname">{{item.json.album.name}}{{item.json.album.alias[0]}}</p>
						<text class="songAuther">{{item.json.album.artists[0].name}}</text>
					</view>
				</view>
				<video src="" v-if="item.json.resource" :poster="item.json.resource.coverUrl" @tap.native.stop="tovideo(item.json.resource.id,item.json.resource.coverUrl)"></video>
				<view class="forward_act" v-if="item.json.event" @tap.native.stop="getdetail(item.json.event,item.json.event.info.threadId)" >
					<view class="fordact_msg">
						<text style="color: cornflowerblue;">@{{item.json.event.user.nickname}}</text>:{{item.json.event.json.msg}}
					</view>
					<view class="imgs" style="display: inline-block;" v-if="item.json.event.pics[0]">
						<img v-for="item2 in item.json.event.pics" :src="item2.pcSquareUrl" class="squareimg" :width="item2.width/15">
					</view>
					
					<view class="audio" v-if="item.json.event.json.song" @tap.native.stop="toSong(item.json.event.json.song.id,item.json.event.json.song.name)">
						<img :src="item.json.event.json.song.album.blurPicUrl">
						<view class="_song">
							<p class="songname">{{item.json.event.json.song.album.name}}{{item.json.event.json.song.album.alias[0]}}</p>
							<text class="songAuther">{{item.json.event.json.song.album.artists[0].name}}</text>
						</view>
					</view>
				</view>
			</view>
			<view class="footer" v-if="item">
				<view class="forward">
					<uni-icons type="redo" @tap.native.stop="shareact(item.id)"></uni-icons>{{item.info.shareCount}}
				</view>
				<view class="comment">
					<uni-icons type="chat"></uni-icons>{{item.info.commentCount}}
				</view>
				<view class="likecount" >
					<uni-icons :color="item.info.liked?'red':''" :type="item.info.liked?'heart-filled':'heart'" @tap.native.stop="like((item.info.liked?0:1),item.info.threadId),(item.info.liked=!item.info.liked),(item.info.liked?(item.info.likedCount++):(item.info.likedCount--))"></uni-icons>{{item.info.likedCount}}
				</view>
				<!-- <view class="share">
					<uni-icons type="paperplane"></uni-icons>
				</view> -->
			</view>
		</view>
	</view>
</template>

<script>
	import {apiBaseUrl} from "../../utils/config.js"
	import squrdetail from "./squrdetail.vue"
	import {getEvent,getTheardId,setLike,getAttentionList,shareAction} from "../../utils/request.js"
	export default {
		components: {
			squrdetail
		},
		data() {
			return{
				attenlist:[],
				acterlist:{},
				lasttime:-1,
				idx:1,
				dix2:0,
				len:0,
				detail:{},
			}
		},
		onLoad() {
			let uid = uni.getStorageSync("uid")
			
			getAttentionList(uid,res =>{
				this.attenlist = res.follow
				console.log(res)
			}),
			getEvent(this.lasttime,res =>{
				console.log(res)
				this.acterlist = res
				this.acterlist.event&&this.acterlist.event.map((item,index) =>{
					item.json =JSON.parse(item.json) 
					if(item.json.event){
						item.json.event.json= JSON.parse(item.json.event.json)
					}
					let arr = item.typeDesc!==null?item.typeDesc:item.info.commentThread.resourceTitle;
					item.typeDesc = arr?.toString().split("：")[0];
				})
				for(let i=0;i<10;i++){
				}
			})
		},
		onReachBottom(){
			getEvent(this.acterlist.lasttime,res =>{
				res.event&&res.event.map((item,index) =>{
					item.json =JSON.parse(item.json)
					 let arr = item.typeDesc!==null?item.typeDesc:item.info.commentThread.resourceTitle;
					 item.typeDesc = arr?.toString().split("：")[0];
				})
				res.event&&res.event.map(item =>this.acterlist.event.push(item)
				)
				this.acterlist.lasttime = res.lasttime
			})
		},
		methods:{
			getdetail(item,threadId){
				getTheardId(threadId,res =>{
					this.detail = res
					uni.setStorageSync('item',JSON.stringify(item));
					uni.setStorageSync('detail',JSON.stringify(this.detail));
				})
				setTimeout(() => {
					uni.navigateTo({
						url: "./squrdetail"
					});
				}, 1000);
			},
			like(t,threadId){
				setLike(t,threadId,res =>{
				})
			},
			shareact(threadId){
				console.log("转发")
				uni.setStorageSync('threadId',threadId);
				setTimeout(() => {
					uni.navigateTo({
						url: "./sharededail"
					});
				}, 1000);
			},
			tovideo(videoId,coverurl){
				setTimeout(() => {
					uni.navigateTo({
						url: "./playvideo?videoid="+videoId+"&coverurl="+coverurl
					});
				}, 1000);
			},
			toSong(songid,name){
				uni.navigateTo({
					url:'/pages/song/song?id='+songid+"&name="+name
				})
			},
			allattent(){
				uni.navigateTo({
					url:'/pages/my/child/followsinger'
				})
			},
			toSinger(id) {
				console.log("歌手id", id);
				uni.navigateTo({
					url: "/pages/singerDetail/singerDetail?id=" + id
				})
			}
		}
		
		
	}
</script>
<style lang="scss">
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
	width: 100%;
	margin: 10rpx auto;
	background-color: #F5F5F5;
}
.left{
	width: 20%;
	display: inline-block;
}
.right{
	width: 80%;
	display: inline-block;
}
.left,.right{
	vertical-align: top;
}
.attention{
	margin: 20rpx;
	text-align: center;
	white-space: nowrap;
}
.mycircle{
	width: 102rpx;
	height: 102rpx;
	margin: 10rpx;
	border-radius: 50%;
	border: 1rpx red solid;
	display: inline-block;
}
.mycircle>img{
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
}
.attention scroll-view{
	white-space: nowrap;
	width: auto;
}
.attent{
	margin: 10rpx;
	display: inline-block;
}
.attenlist{
	width: 102rpx;
	height: 102rpx;
	margin: 10rpx;
	border-radius: 50%;
	border: 1rpx red solid;
	display: inline-block;
	vertical-align: top;
}
.attenlist>img{
	
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
}
.attenname{
	max-width: 100rpx;
	overflow: hidden;
	text-overflow: ellipsis;
}
.actheader{
	
}
._bodytext{
	margin-left: 130rpx;
}
.msg{
	font-size: 16px;
	letter-spacing: 2px;
	word-wrap: break-word;
}
._body{
	margin-left: 130rpx;
}
.userimg{
	margin: 10rpx;
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
}
.actions{
	width: 90%;
	border-bottom: 1rpx #F0F0F0 solid;
	margin: 0rpx auto;
}
.actname{
	display: inline-block;
	padding: 10rpx;
	padding-left: 15rpx;
	padding-top: 20rpx;
	vertical-align: top;
	width: auto;
	font-size: 18px;
	font-weight: 800;
}
._action{
	display: inline-block;
	float: right;
	margin-top: 5px;
	white-space: nowrap;
	font-size: 18px;
	color: #8b8b8b;
}
.link a{
	color: #648CB7;
	font-size: 18px;
	letter-spacing: 3px;
}
.one{
	width: 90%;
}
.tibs a{
	color: #648CB7;
	font-size: 18px;
	letter-spacing: 3px;
}
.squareimg{
	// width: 480rpx;
	// height: 300rpx;
	border-radius: 10px;
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
.forward_act{
	width: 100%;
	padding: 10rpx;
	border-radius: 1em;
	background-color: #e9e9e9;
}
.forward_act>.audio{
	background-color: white;
	width: 90%;
	height: 70%;
	margin: 10rpx auto;
}
video{
	width: 350rpx;
	height: 300rpx;
	border: 1rpx gray solid;
	display: block;
	margin: 10rpx auto;
}
.footer{
	width: 80%;
	margin-left: 20%;
}
.footer>*{
	margin: 20rpx;
	width: 10%;
	display: inline-block;
}
.forward{
	width: 10%;
	display: inline-block;
}
.comment{
	width: 10%;
	display: inline-block;
}
.likecount{
	width: 10%;
	display: inline-block;
}
.share{
	position: absolute;
	right: 20rpx;
}
.forward{
	margin-right: 30px;
}
.comment{
	margin-right: 30px;
}
</style>