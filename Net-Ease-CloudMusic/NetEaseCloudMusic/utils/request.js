import { data } from "uview-ui/libs/mixin/mixin"
import {apiBaseUrl} from "./config.js"
//对象
const post = (url,data="",callback,complete) =>{
	console.log(url,data,callback,complete)
	uni.showLoading({
		title:"加载中"
	})
	uni.request({
		url,
		data:data,
		header:{
			'Accept':"application/json",
			"Content-Type":"application/json"
		},
		method:"POST",
		success:(response)=>{
			const result = response.data
			callback(result)
		},
		complete:(response) =>{
			setTimeout(function(){
				uni.hideLoading();
			},1000)
			complete ? complete() : "";
		},
		fail:(error) =>{
			console.log("网络开小差了")
			uni.showLoading({
				title:"网络开小差了"
			})
			
			uni.hideLoading()
			if(error && error.response){
				showError(error.response)
			}else{
				//....
			}
		}
	})
}
//对象

const get = (url,callback) =>{
	uni.showLoading({
		title:"加载中"
	})
	uni.request({
		url,
		header:{
			'Accept':"application/json",
			"Content-Type":"application/x-www-form-urlencoded",
			"cookie": uni.getStorageSync("cookie")
		},
		method:"GET",
		success:(response)=>{
			const result = response.data
			callback(result)
		},
		complete:(response) =>{
			setTimeout(function(){
				uni.hideLoading();
			},300)
		},
		fail:(error) =>{
			uni.hideLoading()
			if(error && error.response){
				showError(error.response)
			}else{
				//....
			}
		}
	})
}
//封装showError 
const showError = error =>{
	let errorMsg = "";
	switch (error.status){
		case 400:
			errorMsg = "请求参数错误"
			break
		case 401:
			errorMsg = "未授权，请登录"
			break
		case 403:
			errorMsg = "跨域拒绝访问"
			break
		case 404:
			errorMsg = `请求地址出错:${error.config.url}`
			break
		case 408:
			errorMsg = "请求超时"
			break
		case 500:
			errorMsg = "服务器错误"
			break
		case 502:
			errorMsg = "网关错误"
			break
		case 503:
			errorMsg = "服务不可用"
			break
		case 504:
			errorMsg = "网关超时"
			break
		case 505:
			errorMsg = "HTTP版本不受支持"
			break
		default:
			errorMsg = error.msg
			break
	}
	
	uni.showToast({
		title:errorMsg,
		icon:"none",
		duration:1000,
		complete:()=>{
			setTimeout(function(){
				uni.hideToast();
			},1000)
		}
	})
}

//手机号登录
export const login = (phone,password,callback) => get(apiBaseUrl+"/login/cellphone?phone="+phone+"&password="+password,callback)
//获取搜索关键词
export const getSearchDefault = (callback) =>get(apiBaseUrl+"/search/default",callback);
//获取热搜列表
export const getHotList = (callback) =>get(apiBaseUrl+"/search/hot/detail",callback);
// 搜索
export const getSearchList = (params,callback) => get(apiBaseUrl+"/search?keywords="+params,callback)
//获取所有排行榜类型
// export const getTopList = (callback) => get(apiBaseUrl+"/toplist",callback);
//获取对应排行榜歌单---所有歌单都可以
export const getTopListDetail = (id,callback) => get(apiBaseUrl+"/playlist/detail?id="+id,callback)
//获取歌单对应歌曲详情展示成列
export const getSongDetail = (ids,callback) => get(apiBaseUrl+"/song/detail?ids="+ids,callback)
//获取轮播图
export const swipper = (callback) => get(apiBaseUrl+"/banner",callback);
// 获取推荐歌单
export const getPersonalized = (data,callback) => get(apiBaseUrl+"/personalized/?limit="+data,callback);
// 获取歌单详情
export const getListDetail = (data,callback) => get(apiBaseUrl+"/playlist/detail?id="+data,callback);
//歌单添加或删除歌曲
export const addSongCollect = (pid,tracks,callback) => get(apiBaseUrl+"/playlist/tracks?op=add&pid="+pid+"&tracks="+tracks,callback);
//获取用户歌单
export const getSongCollect = (uid,callback) => get(apiBaseUrl+"/user/playlist?uid="+uid,callback);
// 获取每日推荐歌单
export const getRecommend = (callback) => get(apiBaseUrl+"/recommend/songs",callback)
//mv地址
export const playmv = (id,callback) => get(apiBaseUrl+"/mv/url?id="+id,callback)
//获取歌单分类
export const getlistType = (callback) => get(apiBaseUrl+"/playlist/catlist",callback)
//获取热门歌单分类
export const getHotListType = (callback) => get(apiBaseUrl+"/playlist/hot",callback)
// 获取精品歌单
export const gethotList = (data,callback) => get(apiBaseUrl+"/top/playlist/highquality?limit=20&cat="+data,callback)
// 获取歌手分类
export const getSinger = (data,callback) => get(apiBaseUrl+"/artist/list?"+data,callback)
// 获取登录状态
export const loginStatus = (callback) => get(apiBaseUrl+"/login/status",callback)
// 获取歌手热门歌曲
export const getHotSong = (data,callback) => get(apiBaseUrl+"/artist/top/song?id="+data,callback)
// 获取歌手详情 
export const getArtistDetail = (data,callback) => get(apiBaseUrl+"/artist/detail?id="+data,callback)
// 获取歌手粉丝
export const getArtistFans = (data,callback) => get(apiBaseUrl+"/artist/fans?id="+data,callback)
// 获取歌手粉丝数量
export const getFanNum = (data,callback) => get(apiBaseUrl+"/artist/follow/count?id"+data,callback)
// 获取榜单内容
export const getTopList = (callback) => get(apiBaseUrl+"/toplist/detail",callback);
// 获取歌词
export const getlyric = (id,callback) => get(apiBaseUrl+"/lyric?id="+id,callback)
// 获取歌曲的url
export const getsongurl = (id,callback) => get(apiBaseUrl+"/song/url?id="+id,callback)
// 获取歌手全部歌曲 
export const getAllSong = (data,callback) => get(apiBaseUrl+"/artist/songs?id="+data,callback)
//获取用户播放记录
export const getUserRecord = (data,callback) => get(apiBaseUrl+"/user/record?uid="+data,callback)
//获取用户等级
export const getLevel = (data,callback) => get(apiBaseUrl+"/user/level?uid="+data,callback)
// 获取用户粉丝量
export const getFansCount = (data,callback) => get(apiBaseUrl+"/user/followeds?uid="+data,callback)
//获取用户关注量
export const getFocusCount = (data,callback) => get(apiBaseUrl+"/user/follows?uid="+data,callback)
//获取最新视频
export const getNewMv = (sum,callback) =>get(apiBaseUrl+"/mv/exclusive/rcmd?limit="+sum,callback)
//获取视频标签分类列表
export const getGroupMvList = (callback) =>get(apiBaseUrl+"/video/group/list",callback)
//获取分类下的视频
export const getGroupMv = (id,callback) =>get(apiBaseUrl+"/video/group?id="+id,callback)
//获取视频地址
export const getMvAds = (id,callback) =>get(apiBaseUrl+"/mv/url?id="+id,callback)
//获取视频点赞评论
export const getInfo =(id,callback) =>get(apiBaseUrl+"/mv/detail/info?mvid="+id,callback)
//获取动态
export const getEvent =(last,callback) =>get(apiBaseUrl+"/event?pagesize=10&lasttime="+last,callback)
//游客登录
export const getUk =(callback) =>get(apiBaseUrl+"/register/anonimous",callback)
//获取动态评论
export const getTheardId =(id,callback) =>get(apiBaseUrl+"/comment/event?threadId="+id,callback)
//发表动态评论
export const sendActCmt = (id,content,callback) =>get(apiBaseUrl+"/comment?t=1&type=6&threadId="+id+"&content="+content,callback)
//新版获取动态评论
export const getContent = (id,callback) =>get(apiBaseUrl+"/comment/new?type=6&id="+id+"&sortType=2",callback)
//获取喜欢列表
export const getLikeList = (data,callback) => get(apiBaseUrl+"/likelist?uid="+data,callback)
//改变喜欢状态
export const changeLike = (id,data,callback) => get(apiBaseUrl+"/like?id="+id+"&like="+data,callback)
//获取用户动态
export const getuserevent = (uid,callback) => get(apiBaseUrl+"/user/event?uid="+uid,callback)
//删除歌单
export const delplaylist = (id,callback) => get(apiBaseUrl+"/playlist/delete?id="+id,callback)
//点赞动态
export const setLike = (t,id,callback) => get(apiBaseUrl+"/resource/like?t="+t+"&type=6&threadId="+id,callback)
//获取用户关注列表
export const getAttentionList = (id,callback) => get(apiBaseUrl+"/user/follows?uid="+id+"&limit=10",callback)
//转发动态
export const shareAction = (threadId,uid,forwards,callback) =>get(apiBaseUrl+"/event/forward?evId="+threadId+"&uid="+uid+"&forwards="+forwards,callback)
//获取视频详情
export const playvideo =(videoid,callback) =>get(apiBaseUrl+"/video/url?id="+videoid,callback)
//获取用户详情
export const getlikedusers = (uid,callback) =>get(apiBaseUrl+"/user/detail?uid="+uid,callback)
//获取接口调用凭证
export const getTaken = (appid,secret,callback) =>get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret,callback)
//评论过滤
export const msgSecCheck = (taken,data,callback,complete) =>post("https://api.weixin.qq.com/wxa/msg_sec_check?access_token="+taken,data,callback,complete)
//获取用户歌单
export const getUserPlayList = (data,callback) => get(apiBaseUrl+'/user/playlist?uid='+data,callback)
// 新建歌单
export const addPlayList = (data,callback) => get(apiBaseUrl+'/playlist/create?name='+data,callback)
