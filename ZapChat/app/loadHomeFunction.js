// // homeService.js
// import AsyncStorage from "@react-native-async-storage/async-storage";
// import { useState, useEffect, useContext } from "react";
// import { Alert } from "react-native";
// // import { useRouter } from "next/router";
// import { router } from "expo-router";

// export const loadHome = async (
//     {setChatDataArr, setGroupDataArr, 
//     setStatusDataArr, setHeaderImage, 
//     getSearchText, getUser, 
//     setUser, getCategory, getFirstTime, 
//     setFirstTime, setIsFound}) => {

//         console.log("ggggggggggggggggggggggggggggg")
//     try {
//         let parsedUser;

//         if (getUser == null) {
//             let user = await AsyncStorage.getItem("user");

//             parsedUser = JSON.parse(user); // Parse the JSON string to an object
//             setUser(parsedUser); // Set the parsed object in the state

//         } else {
//             parsedUser = getUser
//         }

//         let url = process.env.EXPO_PUBLIC_URL + "/Home"

//         let obj = {
//             searchText: getSearchText,
//             category: getCategory,
//             user: parsedUser
//         }
//         let response = await fetch(url, {
//             method: "POST",
//             body: JSON.stringify(obj),
//             headers: {
//                 "Content-Type": "application/json",
//             }
//         })

//         if (response.ok) {

//             let obj = await response.json()
//             if (obj.success) {

//                 if (getCategory == "chat") {
//                     setChatDataArr(obj.data.data)

//                 } else if (getCategory == "group") {
//                     setGroupDataArr(obj.data.data)
//                 } else if (getCategory == "status") {
//                     setStatusDataArr(obj.data.data)

//                 }
//                 setIsFound(obj.data.isFound)

//                 if (getFirstTime) {

//                     if (obj.data.profile != "../assets/images/default.svg") {
//                         setHeaderImage({ uri: process.env.EXPO_PUBLIC_URL + obj.data.profile })
//                     } else {
//                         setHeaderImage(obj.data.profile)
//                     }
//                     setFirstTime(false)
//                 }

//             } else {
//                 if (obj.data == "Please LogIn") {

//                     await AsyncStorage.removeItem("verified");
//                     await AsyncStorage.removeItem("user");

//                     router.replace("/")
//                 } else {
//                     Alert.alert(obj.data);
//                 }
//                 console.log(obj.data)
//             }

//         } else {
//             Alert.alert("Please Try Again Later");
//             console.log(response)
//         }

//     } catch (error) {
//         console.error(error)
//     }

// }