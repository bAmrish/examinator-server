package com.examinator

import com.examinator.core.UserPaper
import com.examinator.security.authentication.User
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["isFullyAuthenticated()"])
class UserPaperController {

    def userService
    def userPaperService

    def create(){
        String subject = request.JSON["subject"]
        User currentUser = (User) authenticatedUser
        UserPaper paper = userPaperService.generateNewPaper(currentUser, subject)
        Map displayConfig = this.getUserDisplayConfig(params)
        Map paperMap = paper.forDisplay(displayConfig)
        render paperMap as JSON
    }

    def find(){
        String paperId = params["paperId"]
        UserPaper paper = userPaperService.getPaper(paperId)
        Map displayConfig = this.getUserDisplayConfig(params)
        Map paperMap = paper.forDisplay(displayConfig)
        render paperMap as JSON
    }

    def list(){
        User currentUser = (User) authenticatedUser
        List<UserPaper> papers = userPaperService.getAllPapers(currentUser)
        Map displayConfig = this.getUserDisplayConfig(params)
        List<Map> papersDisplayMap = papers.collect { it.forDisplay(displayConfig) }
        render papersDisplayMap as JSON
    }

    private Map getUserDisplayConfig(Map requestParams){
        boolean displayAll = requestParams["displayAll"]
        if(userService.isAdmin() && displayAll) {
            return [
                question : [
                    displayAll: true
                ],
                section: [
                    displayAll: true
                ]
            ]
        }

        return null
    }
}
